## 总结限于java对于多线程的支持，从队列、池和锁几个对象跟踪下去发现的以下几个有用的东西。
注：转自http://www.vmatianyu.cn/summary-of-the-java-thread-concurrency-and-locking.html



FutureTask：

可以取消的异步运算，结合Callable接口，可以轮询是否完成，并且可以取到运算结果。



ReentrantLock：

可重入互斥锁，比使用synchronized方法和语句的隐式锁更强大，可满足基于时限的超时中断的特性，且有公平锁和自由锁，结合Condition可以实现某条件下某线程的挂起和唤醒，并可建立多个等待集。



ReentrantReadWriteLock：

可重入读写锁，比synchronized更高并发性能，ReadLock在没有被释放的时候可以被多个线程同时读取，writeLock在没有被释放的时候其他锁不能进入。



Semaphore：

信号量，可以控制某个资源可被同时访问的个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可。



CountDownLatch：

减数器，countDown方法使计数-1,，await挂起等待，减到零唤醒全部等待线程，不可重用但是多个结合使用可控制较复杂同步事件，强调一个线程等待N个线程。



CyclicBarrier：

关卡，完成既定数目就可以执行某个动作，调用await完成一个任务线程并等待，等待数值-1,等待为零时，触发指定事件，可循环重复使用，强调N个线程相互等。



ArrayBlockingQueue：

数组支持的有界阻塞队列，按 FIFO（先进先出）原则对元素进行排序，达到既定数目，放入线程即阻塞，数目为零拿出线程阻塞。



SynchronousQueue：

是 一种阻塞队列，其中每个 put 必须等待一个 take，反之亦然。同步队列没有任何内部容量，甚至连一个队列的容量都没有。不能在同步队列上进行 peek，因为仅在试图要取得元素时，该元素才存在；除非另一个线程试图移除某个元素，否则也不能（使用任何方法）添加元素；也不能迭代队列，因为其中没有元素可用于迭代。不能在同步队列上进行 peek，因为仅在试图要取得元素时，该元素才存在；
除非另一个线程试图移除某个元素，否则也不能（使用任何方法）添加元素；也不能迭代队列，因为其中没有元素可用于迭代。总之，想送出去，必须有人要才行。



PrioritQueue：

优先级队列，内部对元素采用堆排序，头是指定排序方式的最小元素，堆排序只能保证根是最小的元素（出队元素），堆得依次遍历并不是有序的。



DelayQueue：

延迟队列，集成了PriorityQueue、BlockingQueue和Delay的特性，队头是延迟时间最长的对象，其中的对象只能在其到期时才能从队列中取出。



Executors：

执行者，通过newCachedThreadPool可创建缓冲线程池，可根据需要建新线程，旧线程可用时将重用，针对短期大量异步线程可提高性能。通过newFixedThreadPool可创建固定数量线程池，共享的无界队列方式运行。通过newSingleThreadExecutor创建单个线程。均可配合ThreadFactory创建特定线程，比如设置优先级、守护线程。返回ExecutorService接口的实例。



CompletionServic：

相当于Executor加上BlockingQueue，使用场景为当并发了一系列子线程的任务以后，主线程需要实时地取回子线程任务的返回值并处理这些返回值，谁先返回就先处理谁而不必按照顺序。