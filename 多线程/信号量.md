## 信号量

**Semaphore**当前在多线程环境下被扩放使用，操作系统的信号量是个很重要的概念，在进程控制方面都有应用。Java 并发库 的Semaphore 可以很轻松完成信号量控制，Semaphore可以控制某个资源可被同时访问的个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可。比如在Windows下可以设置共享文件的最大客户端访问个数。 

Semaphore实现的功能就类似厕所有5个坑，假如有10个人要上厕所，那么同时只能有多少个人去上厕所呢？同时只能有5个人能够占用，当5个人中 的任何一个人让开后，其中等待的另外5个人中又有一个人可以占用了。另外等待的5个人中可以是随机获得优先机会，也可以是按照先来后到的顺序获得机会，这取决于构造Semaphore对象时传入的参数选项。单个信号量的Semaphore对象可以实现互斥锁的功能，并且可以是由一个线程获得了“锁”，再由另一个线程释放“锁”，这可应用于死锁恢复的一些场合。

Semaphore维护了当前访问的个数，提供同步机制，控制同时访问的个数。在数据结构中链表可以保存“无限”的节点，用Semaphore可以实现有限大小的链表。另外重入锁 ReentrantLock 也可以实现该功能，但实现上要复杂些。 

下面的Demo中申明了一个只有5个许可的Semaphore，而有20个线程要访问这个资源，通过acquire()和release()获取和释放访问许可。

```java
package com.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TestSemaphore {

    public static void main(String[] args) {
    // 线程池
    ExecutorService exec = Executors.newCachedThreadPool();
    // 只能5个线程同时访问
    final Semaphore semp = new Semaphore(5);
    // 模拟20个客户端访问
      for (int index = 0; index < 20; index++) {
        final int NO = index;
        Runnable run = new Runnable() {
          public void run() {
            try {
              // 获取许可
              semp.acquire();
              System.out.println("Accessing: " + NO);
              Thread.sleep((long) (Math.random() * 10000));

              // 访问完后，释放
              semp.release();
              System.out.println("-----------------"+semp.availablePermits());
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

          }

        };
        exec.execute(run);

      }

      // 退出线程池
      exec.shutdown();

    }

} 

```

二，信号量实现同步问题的处理

1 是如何保证入库时，如果仓库满就等待，

2 出库时，如果仓库无货就等待的。

3 以及对仓库只有10个库位的处理。

4 对同步问题的处理。

```java
public class Xinhaoliang02 {
    public static void main(String[] args) {
        // 启动线程
        for (int i = 0; i <= 3; i++) {
            // 生产者
            new Thread(new Producer()).start();
            // 消费者
            new Thread(new Consumer()).start();
        }
    }
    // 仓库
    static Warehouse buffer = new Warehouse();
    // 生产者，负责增加
    static class Producer implements Runnable {
        static int num = 1;
        @Override
        public void run() {
            int n = num++;
            while (true) {
                try {
                    buffer.put(n);
                    System.out.println(">" + n);
                    // 速度较快。休息10毫秒
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // 消费者，负责减少
    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("<" + buffer.take());
                    // 速度较慢，休息1000毫秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 仓库
     *
     */
    static class Warehouse {
        // 非满锁
        final Semaphore notFull = new Semaphore(10);
        // 非空锁
        final Semaphore notEmpty = new Semaphore(0);
        // 核心锁
        final Semaphore mutex = new Semaphore(1);
        // 库存容量
        final Object[] items = new Object[10];
        int putptr, takeptr, count;
        /**
         * 把商品放入仓库.<br>
         *
         * @param x
         * @throws InterruptedException
         */
        public void put(Object x) throws InterruptedException {
            // 保证非满
            notFull.acquire();
            // 保证不冲突
            mutex.acquire();
            try {
                // 增加库存
                items[putptr] = x;
                if (++putptr == items.length)
                    putptr = 0;
                ++count;
            } finally {
                // 退出核心区
                mutex.release();
                // 增加非空信号量，允许获取商品
                notEmpty.release();
            }
        }
        /**
         * 从仓库获取商品
         *
         * @return
         * @throws InterruptedException
         */
        public Object take() throws InterruptedException {
            // 保证非空
            notEmpty.acquire();
            // 核心区
            mutex.acquire();
            try {
                // 减少库存
                Object x = items[takeptr];
                if (++takeptr == items.length)
                    takeptr = 0;
                --count;
                return x;
            } finally {
                // 退出核心区
                mutex.release();
                // 增加非满的信号量，允许加入商品
                notFull.release();
            }
        }
    }
}

```

三，结合业务，分析

1，之前写过一个爬虫的框架，线程池的线程会去redis队列获取要抓取的url数据，而且这个抓取的代码是写在for循环里面的，当线程池没有空闲线程的时候，会把任务放入线程池框架的阻塞队列中，直到redis队列为空，才会结束任务。
而同时，我会在开一个定时任务去监听队列是否执行完毕，如果队列的数据清空了，则说明任务执行完毕。
那么问题来了，实际情况是，队列被线程池清空了，但是，任务并没有真正的说明被执行完了，有大部分被存放在线程池的缓存队列里面。
所以，可以通过引入信号量，来限制线程池没有空闲线程的时候，不去redis取数据。

下面是示例代码

```java
public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        final Semaphore semaphore = new Semaphore(3);
        for (int i = 1; i <= 20; i++) {
            int no = i;
            try {
                //获得许可
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("从redis队列获取任务-----------------");
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("开始执行任务--------------------" + no);
                        Thread.sleep((long) (Math.random() * 2000));
                        //释放
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            //执行线程
            executor.execute(run);
        }
        //释放线程池
        executor.shutdown();
    }
```

2，微服务的请求限流

当系统访问量很大，已经达到了某个微服务的阈值，这时候就要对外部的请求进行限流，
解决方法就是在dubbo（假设我们使用的是dubbo）提供的方法加上一层注解，利用切面，拦截该方法，进行前置增强，然后使用信号量进行限流。

```java
    private final Semaphore permit = new Semaphore(10, true);
    public void process(){
        try{
            permit.acquire();
            //业务逻辑处理
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            permit.release();
        }
    }
```

