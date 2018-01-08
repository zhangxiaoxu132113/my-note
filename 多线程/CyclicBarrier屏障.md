# CyclicBarrier

###  1、类说明：
一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)。
在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。
因为该 barrier 在释放等待线程后可以重用，所以称它为循环 的 barrier。

### 2、使用场景：
需要所有的子任务都完成时，才执行主任务，这个时候就可以选择使用CyclicBarrier。

### 3、常用方法：
### await

```
public int await()
          throws InterruptedException,
                 BrokenBarrierException
```

### 4、练习例子

旅客观光


``` java

public class CyclicBarrierTest {

	public static void main(String[] args) {
		//阻碍器，只有当线程数达到10，才能执行主任务bus线程
		CyclicBarrier cyclicBarrier = new CyclicBarrier(10, new Bus());
		new Travelers("a", cyclicBarrier).start();
		new Travelers("b", cyclicBarrier).start();
		new Travelers("c", cyclicBarrier).start();
		new Travelers("d", cyclicBarrier).start();
		new Travelers("e", cyclicBarrier).start();
		new Travelers("f", cyclicBarrier).start();
		new Travelers("g", cyclicBarrier).start();
		new Travelers("h", cyclicBarrier).start();
		new Travelers("i", cyclicBarrier).start();
		new Travelers("j", cyclicBarrier).start();
	}
}

/**
 *
* @ClassName: Bus
* @Description: TODO(大巴车)
* @author gh
* @date 2014年12月2日 下午3:00:45
*
 */
class Bus extends Thread{
	public void run(){
		System.out.println("所有人已上车，准备回家");
		System.out.println("开车。。");
	}
}

/**
 *
* @ClassName: Travelers
* @Description: TODO(每个驴友)
* @author gh
* @date 2014年12月2日 下午3:01:01
*
 */
class Travelers extends Thread{
	private String name;
    private CyclicBarrier cb;

    Travelers(String name, CyclicBarrier cb) {
            this.name = name;
            this.cb = cb;
    }
	public void run(){
		System.out.println(name+"开始观光");
		 try {
			 //逛完了，通知大巴车,让大巴等等
			System.out.println(name+"观光完成，准备上车回去");
			cb.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}
```
赛跑的例子



```java
public class CyclicBarrierTest {  
  
    public static void main(String[] args) throws IOException, InterruptedException {  
        //如果将参数改为4，但是下面只加入了3个选手，这永远等待下去  
        //Waits until all parties have invoked await on this barrier.   
        CyclicBarrier barrier = new CyclicBarrier(3);  
  
        ExecutorService executor = Executors.newFixedThreadPool(3);  
        executor.submit(new Thread(new Runner(barrier, "1号选手")));  
        executor.submit(new Thread(new Runner(barrier, "2号选手")));  
        executor.submit(new Thread(new Runner(barrier, "3号选手")));  
  
        executor.shutdown();  
    }  
}  
  
class Runner implements Runnable {  
    // 一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)  
    private CyclicBarrier barrier;  
  
    private String name;  
  
    public Runner(CyclicBarrier barrier, String name) {  
        super();  
        this.barrier = barrier;  
        this.name = name;  
    }  
  
    @Override  
    public void run() {  
        try {  
            Thread.sleep(1000 * (new Random()).nextInt(8));  
            System.out.println(name + " 准备好了...");  
            // barrier的await方法，在所有参与者都已经在此 barrier 上调用 await 方法之前，将一直等待。  
            barrier.await();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        } catch (BrokenBarrierException e) {  
            e.printStackTrace();  
        }  
        System.out.println(name + " 起跑！");  
    }  
}  
```



男友朋友约会的例子

```java
public class cyclicbarTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        executorService.execute(new Girl(cyclicBarrier));
        executorService.execute(new Boy(cyclicBarrier));

    }
}

class Girl implements Runnable {
    private CyclicBarrier cyclicBarrier;

    public Girl(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        try {
            System.out.println("正在画妆");
            Thread.sleep((long) (Math.random() * 2000));
            System.out.println("画妆好了。。。");
            if (cyclicBarrier.getNumberWaiting() != 1) {
                System.out.println("男朋友还没有过来接，现在家里等待！");
            }
            cyclicBarrier.await();
            System.out.println("男朋友来了，跟着男朋友去约会");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class Boy implements Runnable {
    private CyclicBarrier cyclicBarrier;

    public Boy(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        System.out.println("骑着车去接女朋友，正在开车...");
        try {
            Thread.sleep((long) (Math.random() * 20000));
            System.out.println("骑车到了女朋友家了。");
            if (cyclicBarrier.getNumberWaiting() != 1) {
                System.out.println("女朋友正在画妆，在外面等他。");
            }
            cyclicBarrier.await();
            System.out.println("女朋友画妆画好了，跟着女朋友去约会");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

```