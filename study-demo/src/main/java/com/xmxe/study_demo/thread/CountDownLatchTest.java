package com.xmxe.study_demo.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。
 * 是通过一个计数器来实现的，计数器的初始值是线程的数量。每当一个线程执行完毕后，计数器的值就-1，
 * 当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以恢复工作了。
 */
public class CountDownLatchTest {
	public static void main(String[] args) throws InterruptedException{
		CountDownLatch countDown = new CountDownLatch(1);
		CountDownLatch await = new CountDownLatch(4);

		// 局部内部类
		class MyRunnable implements Runnable {
	
			private final CountDownLatch countDown;
			private final CountDownLatch await;
			private final int num;
			
			public MyRunnable(CountDownLatch countDown, CountDownLatch await,int num) {
				this.num = num;
				this.countDown = countDown;
				this.await = await;
			}
			@Override
			public void run() {
				try {
					//等待主线程计数器为0，获得开始执行信号...
					countDown.await();
					System.out.println("处于等待的线程"+num+"开始自己预期工作......");
					//完成预期工作，计数器-1
					await.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// 依次创建并启动处于等待状态的4个MyRunnable线程
		for(int i = 0;i<4;++i){
			new Thread(new MyRunnable(countDown, await, i)).start();
		}
		// for循环执行完在打印线程里面的语句
		countDown.countDown();
		// 主线程阻塞，等待await的计数器为0在往下执行
		await.await();
		// 所有线程执行完在打印Bingo
		System.out.println("Bingo!");
	}

}

/**
 * CyclicBarrier线程会等待，直到足够多线程达到了事先规定的数目。一旦达到触发条件，就可以进行下一步的动作
 */
class CyclicBarrierTest{
	public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,() ->{
            System.out.println("****召唤神龙");
        });
        for(int i = 1;i <= 7; i++){
            int finalI = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 收集到第"+ finalI +"颗龙珠");
                try {
					cyclicBarrier.await();
					System.out.println(finalI+"开始许愿了");
                } catch (Exception e) {
                    e.printStackTrace();
                } 
            },"第"+i+"个线程").start();
        }
    }
}
/**
 * CyclicBarrier与CountDownLatch的区别
 * 这两个类都可以实现一组线程在到达某个条件之前进行等待，它们内部都有一个计数器，当计数器的值不断的减为0的时候所有阻塞的线程将会被唤醒。
 * 有区别的是CyclicBarrier的计数器由自己控制，而CountDownLatch的计数器则由使用者来控制，
 * 在CyclicBarrier中线程调用await方法不仅会将自己阻塞还会将计数器减1，而在CountDownLatch中线程调用await方法只是将自己阻塞而不会减少计数器的值。
 * CountDownLatch只能拦截一轮，而CyclicBarrier可以实现循环拦截
 */

 /**
  * Semaphore	信号量，可以通过控制“许可证”的数量，来保证线程之间的配合 线程只有拿到“许可证”后才能继续运行，相比于其它的同步器，更灵活
  * CyclicBarrier线程会等待，直到足够多线程达到了事先规定的数目。一旦达到触发条件，就可以进行下一步的动作 适用于线程之间相互等待处理结果的就绪场景
  * Phaser和CyclicBarrier类似，但是计数可变	Java7加入的
  * CountDownLatch和CyclicBarrier类似，数量递减到0时，触发动作	不可重复使用
  * Exchanger让两个线程在合适时交换对象	适用场景：当两个线程工作在同一个类的不同实例上时，用于交换数据
  * Condition 可以控制线程的“等待”和“唤醒”	是Object.wait() 的升级版
  */