## 2种办法让HashMap线程安全
HashMap不是线程安全的,往往在写程序时需要通过一些方法来回避.其实JDK原生的提供了2种方法让HashMap支持线程安全.

方法一:通过Collections.synchronizedMap()返回一个新的Map,这个新的map就是线程安全的. 这个要求大家习惯基于接口编程,因为返回的并不是HashMap,而是一个Map的实现.
方法二:重新改写了HashMap,具体的可以查看java.util.concurrent.ConcurrentHashMap. 这个方法比方法一有了很大的改进.

下面对这2中实现方法从各个角度进行分析和比较.

实现原理
锁机制的不同
如何得到/释放锁
优缺点


1)实现原理

方法一原理:

通过Collections.synchronizedMap()来封装所有不安全的HashMap的方法,就连toString, hashCode都进行了封装. 封装的关键点有2处,1)使用了经典的synchronized来进行互斥, 2)使用了代理模式new了一个新的类,这个类同样实现了Map接口.


```java
private static class SynchronizedMap<K,V>
implements Map<K,V>, Serializable {
// use serialVersionUID from JDK 1.2.2 for interoperability

private static final long serialVersionUID = 1978198479659022715L;

private final Map<K,V> m;     // Backing Map

final Object      mutex;// Object on which to synchronize

SynchronizedMap(Map<K,V> m) {

if (m==null)

throw new NullPointerException();

this.m = m;

mutex = this;

}



SynchronizedMap(Map<K,V> m, Object mutex) {

this.m = m;

this.mutex = mutex;

}



public int size() {

synchronized(mutex) {return m.size();}

}



//***********************************

//节省空间,删除了大量类似代码

//***********************************

public String toString() {

synchronized(mutex) {return m.toString();}

}

private void writeObject(ObjectOutputStream s) throws IOException {

synchronized(mutex) {s.defaultWriteObject();}

}

}
```




方法二原理:

重新写了HashMap,比较大的改变有如下几点.
使用了新的锁机制(可以理解为乐观锁)稍后详细介绍
把HashMap进行了拆分,拆分成了多个独立的块,这样在高并发的情况下减少了锁冲突的可能
```java
public V put(K key, V value) {
    if (value == null)
        throw new NullPointerException();
    int hash = hash(key.hashCode());
    return segmentFor(hash).put(key, hash, value, false);

}
```

2)锁机制的不同
方法一使用的是的synchronized方法,是一种悲观锁.在进入之前需要获得锁,确保独享当前对象,然后做相应的修改/读取.
方法二使用的是乐观锁,只有在需要修改对象时,比较和之前的值是否被人修改了,如果被其他线程修改了,那么就会返回失败.锁的实现,使用的是NonfairSync. 这个特性要确保修改的原子性,互斥性,无法在JDK这个级别得到解决,JDK在此次需要调用JNI方法,而JNI则调用CAS指令来确保原子性与互斥性.读者可以自行Google JAVA CAS来了解更多. JAVA的乐观锁是如何实现的.
当如果多个线程恰好操作到ConcurrentHashMap同一个segment上面,那么只会有一个线程得到运行,其他的线程会被LockSupport.park(),稍后执行完成后,会自动挑选一个线程来执行LockSupport.unpark().

```java
V put(K key, int hash, V value, boolean onlyIfAbsent) {

    lock();

    try {

        int c = count;

        if (c++ > threshold) // ensure capacity

            rehash();

        HashEntry<K,V>[] tab = table;

        int index = hash & (tab.length - 1);

        HashEntry<K,V> first = tab[index];

        HashEntry<K,V> e = first;

        while (e != null && (e.hash != hash || !key.equals(e.key)))

            e = e.next;



        V oldValue;

        if (e != null) {

            oldValue = e.value;

            if (!onlyIfAbsent)

                e.value = value;

        }

        else {

            oldValue = null;

            ++modCount;

            tab[index] = new HashEntry<K,V>(key, hash, first, value);

            count = c; // write-volatile

        }

        return oldValue;

    } finally {

        unlock();

    }

}
```

3)如何得到/释放锁

得到锁:

方法一:在Hashmap上面,synchronized锁住的是对象(不是Class),所以第一个申请的得到锁,其他线程将进入阻塞,等待唤醒.

方法二:检查AbstractQueuedSynchronizer.state,如果为0,则得到锁,或者申请者已经得到锁,则也能再辞得到锁,并且state也加1.

释放锁:

都是得到锁的逆操作,并且使用正确,二种方法都是自动选取一个队列中的线程得到锁可以获得CPU资源.



4)优缺点

方法一:

优点:代码实现十分简单,一看就懂.

缺点:从锁的角度来看,方法一直接使用了锁住方法,基本上是锁住了尽可能大的代码块.性能会比较差.



方法二:

优点:需要互斥的代码段比较少,性能会比较好. ConcurrentHashMap把整个Map切分成了多个块,发生锁碰撞的几率大大降低,性能会比较好.

缺点:代码实现稍稍复杂些.