## 高并发容器 之 BlockingQueue

#### BlockingQueue 阻塞队列
在jdk并发包中进入了阻塞队列，一共有五种实现，常见的方法如下
> add(E o); //将指定的元素添加到此队列中（如果立即可行），在成功时返回 true，其他情况则抛出 IllegalStateException。
> drainTo(Collection<? super E> c);  //移除此队列中所有可用的元素，并将它们添加到给定 collection 中。
> drainTo(Collection<? super E> c,int maxElements);//最多从此队列中移除给定数量的可用元素，并将这些元素添加到给定 collection 中
> offer(E o);  //如果可能的话，将指定元素插入此队列中。
> offer(E o, long timeout, TimeUnit unit);  //将指定的元素插入此队列中，如果没有可用空间，将等待指定的等待时间（如果有必要）。
> poll(long timeout, TimeUnit unit);  //检索并移除此队列的头部，如果此队列中没有任何元素，则等待指定等待的时间（如果有必要）。
> put(E o);    //将指定元素添加到此队列中，如果没有可用空间，将一直等待（如果有必要）。
> remainingCapacity();  //返回在无阻塞的理想情况下（不存在内存或资源约束）此队列能接受的元素数量；如果没有内部限制，则返回 Integer.MAX_VALUE。
> take();  //检索并移除此队列的头部，如果此队列不存在任何元素，则一直等待。

- *LinkedBlockingQueue*
LinkedBlockingQueue（说的不准确，在不指定时容量为Integer.MAX_VALUE，不要然的话在put时怎么会受阻呢），但是也可以选择指定其最大容量，它是基于链表的队列，此队列按 FIFO（先进先出）排序元素。
下面摘用了jdk的一段源码，线程池的缓冲队列就是使用了LinkedBlockingQueue来工作的。所以线程池的任务队列是
```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}
```


- *ArrayBlockingQueue*
ArrayBlockingQueue在构造时需要指定容量， 并可以选择是否需要公平性，如果公平参数被设置true，等待时间最长的线程会优先得到处理（其实就是通过将ReentrantLock设置为true来 达到这种公平性的：即等待时间最长的线程会先操作）。
通常，公平性会使你在性能上付出代价，只有在的确非常需要的时候再使用它。它是基于数组的阻塞循环队 列，此队列按 FIFO（先进先出）原则对元素进行排序。
总结两点：一个是有界的，一个是选择是否公平。
这里需要看下ReentrantLock的公平锁和非公平锁
```java
ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(3);
queue.add("str");
queue.add("abc");
queue.add("vds");
queue.add("bcs");
/**
 * Exception in thread "main" java.lang.IllegalStateException: Queue full
 */
for (Iterator<String> iterator = queue.iterator(); iterator.hasNext();) {
    System.out.println(iterator.next());
}
```
- *PriorityBlockingQueue*
PriorityBlockingQueue是一个带优先级的 队列，而不是先进先出队列。元素按优先级顺序被移除，该队列也没有上限（看了一下源码，PriorityBlockingQueue是对 PriorityQueue的再次包装，是基于堆数据结构的，而PriorityQueue是没有容量限制的，与ArrayList一样，所以在优先阻塞 队列上put时是不会受阻的。虽然此队列逻辑上是无界的，但是由于资源被耗尽，所以试图执行添加操作可能会导致 OutOfMemoryError），但是如果队列为空，那么取元素的操作take就会阻塞，所以它的检索操作take是受阻的。另外，往入该队列中的元 素要具有比较能力。
```java
public class UseProBlockingQueue {

    public static void main(String[] args) {
        PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue();
        Task task1 = new Task();
        task1.setId(3);
        task1.setName("任务1");
        Task task2 = new Task();
        task2.setId(1);
        task2.setName("任务2");
        Task task3 = new Task();
        task3.setId(2);
        task3.setName("任务3");

        priorityBlockingQueue.add(task1);
        priorityBlockingQueue.add(task2);
        priorityBlockingQueue.add(task3);

        try {
            priorityBlockingQueue.take();
            System.out.println(priorityBlockingQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Task implements Comparable<com.water.pinfangduilie.Task> {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int compareTo(com.water.pinfangduilie.Task task) {
            return this.getId() > task.getId() ? 1 : (this.getId() < task.getId() ? -1 : 0);
        }

        @Override
        public String toString() {
            return "Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}

```


- *DelayQueue*
DelayQueue（基于PriorityQueue来实现的）是一个存放Delayed 元素的无界阻塞队列，只有在延迟期满时才能从中提取元素。该队列的头部是延迟期满后保存时间最长的 Delayed 元素。如果延迟都还没有期满，则队列没有头部，并且poll将返回null。当一个元素的 getDelay(TimeUnit.NANOSECONDS) 方法返回一个小于或等于零的值时，则出现期满，poll就以移除这个元素了。此队列不允许使用 null 元素。
a) 关闭空闲连接。服务器中，有很多客户端的连接，空闲一段时间之后需要关闭之。
b) 缓存。缓存中的对象，超过了空闲时间，需要从缓存中移出。
c) 任务超时处理。在网络协议滑动窗口请求应答式交互时，处理超时未响应的请求。

