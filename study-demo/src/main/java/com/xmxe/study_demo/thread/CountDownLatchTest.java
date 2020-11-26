package com.xmxe.study_demo.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
	public static void main(String[] args) throws InterruptedException{
		CountDownLatch countDown = new CountDownLatch(1);
		CountDownLatch await = new CountDownLatch(4);

		// 依次创建并启动处于等待状态的100个MyRunnable线程
		for(int i = 0;i<100;++i){
			new Thread(new MyRunnable(countDown, await, i)).start();
		}
		// 
		countDown.countDown();
		await.await();
		System.out.println("Bingo!");
	}

}

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
			await.countDown();//完成预期工作，发出完成信号...
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
