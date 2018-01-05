## 线程间通信Condition

Condition可以替代传统的线程间通信，**用await()替换wait()，用signal()替换notify()，用signalAll()替换notifyAll()。**

——为什么方法名不直接叫wait()/notify()/nofityAll()？因为Object的这几个方法是final的，不可重写！

传统线程的通信方式，Condition都可以实现。

注意，**Condition是被绑定到Lock上的**，要创建一个Lock的Condition必须用newCondition()方法。

Condition的强大之处在于它可以为多个线程间建立不同的Condition

看JDK文档中的一个例子：假定有一个绑定的缓冲区，它支持 put 和 take 方法。如果试图在空的缓冲区上执行 take 操作，则在某一个项变得可用之前，线程将一直阻塞；如果试图在满的缓冲区上执行 put 操作，则在有空间变得可用之前，线程将一直阻塞。我们喜欢在单独的等待 set 中保存put 线程和take 线程，这样就可以在缓冲区中的项或空间变得可用时利用最佳规划，一次只通知一个线程。可以使用两个[`Condition`](http://blog.csdn.net/java/util/concurrent/locks/Condition.html) 实例来做到这一点。

——其实就是java.util.concurrent.ArrayBlockingQueue的功能

```java
class BoundedBuffer {  
  final Lock lock = new ReentrantLock();          //锁对象  
  final Condition notFull  = lock.newCondition(); //写线程锁  
  final Condition notEmpty = lock.newCondition(); //读线程锁  
  
  final Object[] items = new Object[100];//缓存队列  
  int putptr;  //写索引  
  int takeptr; //读索引  
  int count;   //队列中数据数目  
  
  //写  
  public void put(Object x) throws InterruptedException {  
    lock.lock(); //锁定  
    try {  
      // 如果队列满，则阻塞<写线程>  
      while (count == items.length) {  
        notFull.await();   
      }  
      // 写入队列，并更新写索引  
      items[putptr] = x;   
      if (++putptr == items.length) putptr = 0;   
      ++count;  
  
      // 唤醒<读线程>  
      notEmpty.signal();   
    } finally {   
      lock.unlock();//解除锁定   
    }   
  }  
  
  //读   
  public Object take() throws InterruptedException {   
    lock.lock(); //锁定   
    try {  
      // 如果队列空，则阻塞<读线程>  
      while (count == 0) {  
         notEmpty.await();  
      }  
  
      //读取队列，并更新读索引  
      Object x = items[takeptr];   
      if (++takeptr == items.length) takeptr = 0;  
      --count;  
  
      // 唤醒<写线程>  
      notFull.signal();   
      return x;   
    } finally {   
      lock.unlock();//解除锁定   
    }   
  }  
```

### 优点：

假设缓存队列中已经存满，那么阻塞的肯定是写线程，唤醒的肯定是读线程，相反，阻塞的肯定是读线程，唤醒的肯定是写线程。
那么假设只有一个Condition会有什么效果呢？缓存队列中已经存满，这个Lock不知道唤醒的是读线程还是写线程了，如果唤醒的是读线程，皆大欢喜，如果唤醒的是写线程，那么线程刚被唤醒，又被阻塞了，这时又去唤醒，这样就浪费了很多时间。

