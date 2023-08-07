package com.xmxe.jdkfeature.thread;

public class VolatileTest {
	private static boolean stop = false;

	// 不加volatile关键字运行main方法会进入死循环 因为线程1是从cpu高速缓存中获取stop变量,加上volatile关键字则从主内存中获取stop变量
	// private static volatile boolean stop = false;

	public static void main(String[] args) throws Exception {
		Thread ta = new Thread(() -> {
			while (!stop) {
				// String a = "a";
				// System.out.println(a);
				/**
				 * System.out.println()会影响volatile可见性,因为println()使用synchronized修饰的,JMM关于synchronized的两条规定:
				 * 1.线程解锁前,必须把共享变量的最新值刷新到主内存中
				 * 2.线程加锁时,将清空工作内存中共享变量的值,从而使用共享变量时需要从主内存中重新获取最新的值
				 */
			}
		}, "线程1");
		ta.start();
		Thread.sleep(100);
		Thread tb = new Thread(() -> {
			System.out.println("do stop");
			stop = true;
		}, "线程2");
		tb.start();
	}

}

class VolatileTest2 {
	private static volatile int MY_INT = 0;

	public static void main(String[] args) {
		// 主线程启动两个线程,线程1负责对MY_INT值改变的侦听,如果有改变就会打印出来,线程2负责改变MY_INT的值
		new Thread(() -> {
			int local_value = MY_INT;
			while (local_value < 5) {
				if (local_value != MY_INT) {
					System.out.println("Got Change for MY_INT : " + MY_INT);
					local_value = MY_INT;
				}
			}
		}, "线程1").start();

		new Thread(() -> {
			int local_value = MY_INT;
			while (MY_INT < 5) {
				System.out.println("Incrementing MY_INT to " + (local_value + 1));
				MY_INT = ++local_value;
				try {
					// Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, "线程2").start();
	}
}