public class NotifyTestString {

    public static class Thread1 implements Runnable {
        public String lock;
        public String lockAfter;

        public Thread1(String lock, String lockAfter) {
            this.lock = lock;
            this.lockAfter = lockAfter;
        }

        public void run() {
            synchronized (lock) {
                synchronized (lockAfter) {
                    while (true) {
                        lockAfter.notify();
                        System.out.println(" lockAfter notified! ");
                        try {
                            System.out.println("try to wait!");
                            lock.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println("the end of  synchronized code locked by lockAfter!");
                    }
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        String lockAfter = "";
        String lock = "";
        Thread1 t1 = new Thread1(lock, lockAfter);
        new Thread(t1).start();
        Thread.currentThread().sleep(100);
        synchronized (lockAfter) {
            System.out.println("gain the lock");
        }

        /**
         *  这行代码输出的结果如下：
         *  lockAfter notified!
         *  try to wait!
         *  gain the lock
         *
         *  （理解这个段代码的前提就是wait是释放锁的，notify是不释放锁的）
         *  也许好奇的地方是lock释放了锁，为什么lockAfter可以得到锁呢（lockAfter.notify();虽然lockAfter已经notify了，但是锁并没有释放）
         *  其实原因就是
         *
         *  自己的两个String是同一个对象，因为如果是直接将字符串赋给一个String变量的话，
         *  jvm会首先在字符串池中看看有没有，没有的话，就创建一个字符串，放进字符串池中，
         *  然后返回引用，如果有的话，就返回池中的这个，我这样声明的两个字符串其实是一个，
         *  就是说，两次获得的锁其实是一个，这就是为什么锁获得到的原因，
         *
         */
    }
