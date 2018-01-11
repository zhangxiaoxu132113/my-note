## 谈谈JDK线程的伪唤醒
    在JDK的官方的wait()方法的注释中明确表示线程可能被“虚假唤醒“，JDK也明确推荐使用while来判断状态信息。那么这种情况的发生的可能性有多大呢？

    使用生产者消费者模型来说明，伪唤醒造成的后果是本来未被唤醒的线程被唤醒了，那么就破坏了生产者消费者中的判断条件，也就是例子中的while条件number == 0或者number == 1。最终导致的结果就死0和1不能交替出现。

    JDK的两种同步方案均可能出现这种伪唤醒的问题（API说明明确表示会出现这种现象），这两种组合是synchronized+wait+notify和ReentrantLock+await+signal。下面的例子中，如果把while换成if，那么就0、1就不能交替出现，反制则会，例子中是100个线程进行增加，100个线程进行减少。

    在Java并发编程书上面引用了Thinking In Java的一句话说，大概意思是：任何并发编程都是通过加锁来解决。其实JDK并发编程也是通过加锁解决，每个对象都有一个对象锁，并且有一个与这个锁相关的队列，来实现并发编程。区别在于加锁的粒度问题，读读可以并发（在读远大于写的场景下比较合适），其他三种情况不能并发。而关系型数据库也是利用这一思想，只不过做得更加彻底，除了写写不能并发，其他三种情况都能并发，这得益于MVCC模型。

```java
public class Resource {
    public int number = 0;
    public synchronized void add() {
        while (number == 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        number++;
        System.err.println(number + "-" + Thread.currentThread().getId());
        notifyAll();
    }

    public synchronized void minus() {
        while (number == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        number--;
        System.err.println(number + "-" + Thread.currentThread().getId());
        notifyAll();
    }

    public static class AddThread implements Runnable {

        private Resource resource;

        public AddThread(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (;;) resource.add();
        }
    }

    public static class MinusThread implements Runnable {
        private Resource resource;

        public MinusThread(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (;;) resource.minus();
        }
    }

    public static void main(String[] args) {
        Resource resource = new Resource();
        for (int i = 0; i < 100; i++) {
            new Thread(new AddThread(resource)).start();
            new Thread(new MinusThread(resource)).start();
        }
    }
}
```
``` java
public class ResourceLock {

    private int number = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public int getNumber() {
        return this.number;
    }

    public void increase() {
        lock.lock();
        try {
            while (number == 1) {
                condition.await();
            }
            number++;
            System.err.println(number + "-" + Thread.currentThread().getId());
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void decrease() {
        lock.lock();
        try {
            while (number == 0) {
                condition.await();
            }
            number--;
            System.err.println(number + "-" + Thread.currentThread().getId());
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    static class IncreaseThread implements Runnable {
        private ResourceLock resource;

        public IncreaseThread(ResourceLock resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (;;) resource.increase();
        }
    }

    static class DecreaseThread implements Runnable {
        private ResourceLock resource;

        public DecreaseThread(ResourceLock resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (;;) resource.decrease();
        }
    }

    public static void main(String[] args) throws Exception {
        ResourceLock resource = new ResourceLock();
        ExecutorService es = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 100; i++) {
            es.submit(new IncreaseThread(resource));
            es.submit(new DecreaseThread(resource));
        }
    }

}
```