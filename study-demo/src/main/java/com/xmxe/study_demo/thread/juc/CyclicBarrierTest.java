package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier与CountDownLatch的区别
 * 1.用途：
 * CountDownLatch被用于等待一个或多个线程完成操作后再继续执行，允许一个或多个线程等待其他线程的信号。
 * CyclicBarrier被用于等待一组线程到达某个屏障点后再同时继续执行，允许多个线程互相等待对方。
 * 2.计数方式：
 * CountDownLatch内部维护一个计数器，当计数器达到零时，阻塞在await()方法上的线程将继续执行。计数器的初始值由构造函数指定，通过countDown()方法递减计数器。
 * CyclicBarrier也内部维护一个计数器，当计数器达到屏障点时，所有线程都将释放并同时执行。计数器的初始值也由构造函数指定，通过await()方法递增计数器。
 * CyclicBarrier的计数器由自己控制，而CountDownLatch的计数器则由使用者来控制，
 * 3.可重用性：
 * CountDownLatch是一次性的：一旦计数器达到零，它不能被重置。如果你需要多次等待操作完成，就需要重新创建一个新的CountDownLatch实例。
 * CyclicBarrier是可以重用的：一旦所有线程到达屏障点并释放，计数器将重置并可以再次使用。
 * CountDownLatch只能拦截一轮，而CyclicBarrier可以实现循环拦截
 * 4.同步机制：
 * CountDownLatch主要通过await()和countDown()方法实现线程之间的等待和通知。
 * CyclicBarrier主要通过await()方法实现线程之间的等待和同步，并且可以通过await()方法的返回值触发指定线程的回调。
 * 
 * Phaser和CyclicBarrier类似，但是计数可变.Java7加入的
 * CyclicBarrier线程会等待，直到足够多线程达到了事先规定的数目。一旦达到触发条件，就可以进行下一步的动作,它的作用就是会让所有线程都等待完成后才会继续下一步行动
 * 
 * int await():等待所有parties(表示屏障拦截的线程数量)已经在这个障碍上调用了await。
 * int await(long timeout, TimeUnit unit):等待所有parties已经在此屏障上调用await，或指定的等待时间过去。
 * int getNumberWaiting():返回目前正在等待障碍的各方的数量。
 * int getParties():返回旅行这个障碍所需的parties数量。
 * boolean isBroken():查询这个障碍是否处于破碎状态。
 * void reset():将屏障重置为初始状态。
 */
public class CyclicBarrierTest {
    public static void main(String[] args) {
        demo1();
    }

    public static void demo1() {
        // parties表示屏障拦截的线程数量，当屏障撤销时，先执行barrierAction，然后在释放所有线程
        // public CyclicBarrier(int parties, Runnable barrierAction)
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            System.out.println("****召唤神龙*****");
        });
        for (int i = 1; i <= 7; i++) {
            int finalI = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 收集到第" + finalI + "颗龙珠");
                try {
                    cyclicBarrier.await();
                    System.out.println(finalI + "开始许愿了");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "第" + i + "个线程").start();
        }
    }

}
