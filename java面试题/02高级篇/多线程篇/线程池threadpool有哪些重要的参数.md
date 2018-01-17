创建线程池
```java
//参数初始化
private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
//核心线程数量大小
private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
//线程池最大容纳线程数
private static final int maximumPoolSize = CPU_COUNT * 2 + 1;
//线程空闲后的存活时长
private static final int keepAliveTime = 30;

//任务过多后，存储任务的一个阻塞队列
BlockingQueue<Runnable>  workQueue = new SynchronousQueue<>();

//线程的创建工厂
ThreadFactory threadFactory = new ThreadFactory() {
    private final AtomicInteger mCount = new AtomicInteger(1);

    public Thread newThread(Runnable r) {
        return new Thread(r, "AdvacnedAsyncTask #" + mCount.getAndIncrement());
    }
};

//线程池任务满载后采取的任务拒绝策略
RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

//线程池对象，创建线程
ThreadPoolExecutor mExecute = new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        workQueue,
        threadFactory,
        rejectHandler
);
```

具体参数介绍
corePoolSize
线程池的核心线程数。在没有设置 allowCoreThreadTimeOut 为 true 的情况下，核心线程会在线程池中一直存活，即使处于闲置状态。

maximumPoolSize
线程池所能容纳的最大线程数。当活动线程(核心线程+非核心线程)达到这个数值后，后续任务将会根据 RejectedExecutionHandler 来进行拒绝策略处理。

keepAliveTime
非核心线程闲置时的超时时长。超过该时长，非核心线程就会被回收。若线程池通过 allowCoreThreadTimeOut() 方法设置 allowCoreThreadTimeOut 属性为 true，则该时长同样会作用于核心线程，AsyncTask 配置的线程池就是这样设置的。

unit
keepAliveTime 时长对应的单位。

workQueue
线程池中的任务队列，通过线程池的 execute() 方法提交的 Runnable 对象会存储在该队列中。

ThreadFactory
线程工厂，功能很简单，就是为线程池提供创建新线程的功能。这是一个接口，可以通过自定义，做一些自定义线程名的操作。

RejectedExecutionHandler
当任务无法被执行时(超过线程最大容量 maximum 并且 workQueue 已经被排满了)的处理策略，这里有四种任务拒绝类型。

线程池工作原则
1、当线程池中线程数量小于 corePoolSize 则创建线程，并处理请求。
2、当线程池中线程数量大于等于 corePoolSize 时，则把请求放入 workQueue 中,随着线程池中的核心线程们不断执行任务，只要线程池中有空闲的核心线程，线程池就从 workQueue 中取任务并处理。
3 、当 workQueue 已存满，放不下新任务时则新建非核心线程入池，并处理请求直到线程数目达到 maximumPoolSize（最大线程数量设置值）。
4、如果线程池中线程数大于 maximumPoolSize 则使用 RejectedExecutionHandler 来进行任务拒绝处理。
任务队列 BlockingQueue
任务队列 workQueue 是用于存放不能被及时处理掉的任务的一个队列，它是 一个 BlockingQueue 类型。

关于 BlockingQueue，虽然它是 Queue 的子接口，但是它的主要作用并不是容器，而是作为线程同步的工具，他有一个特征，当生产者试图向 BlockingQueue 放入(put)元素，如果队列已满，则该线程被阻塞；当消费者试图从 BlockingQueue 取出(take)元素，如果队列已空，则该线程被阻塞。(From 疯狂Java讲义)

任务拒绝类型(拒绝策略)
ThreadPoolExecutor.AbortPolicy:
当线程池中的数量等于最大线程数时抛 java.util.concurrent.RejectedExecutionException 异常，涉及到该异常的任务也不会被执行，线程池默认的拒绝策略就是该策略。

ThreadPoolExecutor.DiscardPolicy():
当线程池中的数量等于最大线程数时,默默丢弃不能执行的新加任务，不报任何异常。

ThreadPoolExecutor.CallerRunsPolicy():
当线程池中的数量等于最大线程数时，重试添加当前的任务；它会自动重复调用execute()方法。

ThreadPoolExecutor.DiscardOldestPolicy():
当线程池中的数量等于最大线程数时,抛弃线程池中工作队列头部的任务(即等待时间最久的任务)，并执行新传入的任务。

下面的链接讲述了线程池的拒绝策略:http://blog.csdn.net/lfdfhl/article/details/40739093