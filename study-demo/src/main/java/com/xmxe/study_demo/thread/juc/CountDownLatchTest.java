package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。是通过一个计数器来实现的，计数器的初始值是线程的数量。
 * 每当一个线程执行完毕后，计数器的值就-1，当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以恢复工作了。
 * 
 * await():调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
 * await(long timeout, TimeUnit unit):和await()类似，只不过等待一定的时间后count值还没变为0的话就会继续执行
 * countDown();将count值减1
 */
public class CountDownLatchTest {
	public static void main(String[] args) {
		// demo1();
		demo2();
	}

	public static void demo1() {
		CountDownLatch countDown = new CountDownLatch(1);
		CountDownLatch await = new CountDownLatch(4);

		// 局部内部类
		class MyRunnable implements Runnable {

			private final CountDownLatch countDown;
			private final CountDownLatch await;
			private final int num;

			public MyRunnable(CountDownLatch countDown, CountDownLatch await, int num) {
				this.num = num;
				this.countDown = countDown;
				this.await = await;
			}

			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getName() + "准备阻塞,当countDown计数器为0时才继续往下执行");
					// 等待主线程计数器为0，获得开始执行信号...
					countDown.await();
					System.out.println(Thread.currentThread().getName() + "处于等待的线程" + num + "开始自己预期工作......");
					// 完成预期工作，计数器-1
					await.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// 依次创建并启动处于等待状态的4个MyRunnable线程
		for (int i = 0; i < 4; ++i) {
			new Thread(new MyRunnable(countDown, await, i), "线程" + i).start();
		}
		// for循环执行完在打印子线程里面的语句
		countDown.countDown();
		// 主线程阻塞，等待await的计数器为0在往下执行
		try {
			await.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 所有子线程执行完在打印Bingo
		System.out.println("Bingo!");
	}

	/**
	 * 一种典型的场景就是火箭发射。在火箭发射前，为了保证万无一失，往往还要进行各项设备、仪器的检查。只有等所有检查完毕后，引擎才能点火。这种场景就非常适合使用CountDownLatch。
	 * 它可以使得点火线程,等待所有检查线程全部完工后，再执行
	 */
	public static void demo2() {
		CountDownLatch end = new CountDownLatch(10);
		ExecutorService service = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			service.submit(() -> {
				System.out.println(Thread.currentThread().getName() + "check complete!");
				end.countDown();
			}, "线程" + i);
		}
		// 等待检查,所有线程都执行完毕才继续往下执行
		try {
			end.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 所有线程检查完毕,发射火箭.
		System.out.println("fire");
		service.shutdown();
	}

}