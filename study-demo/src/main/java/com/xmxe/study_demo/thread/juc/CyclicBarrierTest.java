package com.xmxe.study_demo.thread.juc;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier线程会等待，直到足够多线程达到了事先规定的数目。一旦达到触发条件，就可以进行下一步的动作,它的作用就是会让所有线程都等待完成后才会继续下一步行动
 * 
 * int await() 等待所有parties已经在这个障碍上调用了await 。
 * int await(long timeout, TimeUnit unit) 等待所有parties已经在此屏障上调用await ，或指定的等待时间过去。
 * int getNumberWaiting() 返回目前正在等待障碍的各方的数量。
 * int getParties() 返回旅行这个障碍所需的parties数量。
 * boolean isBroken() 查询这个障碍是否处于破碎状态。
 * void reset() 将屏障重置为初始状态。
 */
public class CyclicBarrierTest{
	public static void main(String[] args) {
        // demo1();
        demo2();
    }

    public static void demo1(){
        // parties表示屏障拦截的线程数量，当屏障撤销时，先执行barrierAction，然后在释放所有线程 
        // public CyclicBarrier(int parties, Runnable barrierAction)
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,() ->{
            System.out.println("****召唤神龙*****");
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

    public static void demo2(){
        final int N = 5;
        Thread[] allSoldier = new Thread[N];
        boolean flag = false;

        // ------BarrierRun start----
        class BarrierRun implements Runnable {
            boolean flag;
            int N;
    
            public BarrierRun(boolean flag, int n) {
                this.flag = flag;
                N = n;
            }
    
            @Override
            public void run() {
                if (flag) {
                    System.out.println("士兵:" + N + "个, 任务完成!");
                } else {
                    System.out.println("士兵:" + N + "个, 集合完毕!");
                    flag = true;
                }
            }
        }
        // ------BarrierRun end-----

        CyclicBarrier cyclic = new CyclicBarrier(N, new BarrierRun(flag, N));
        // 设置屏障点, 主要为了执行这个方法.
        System.out.println("请求集合！！！!");
        for (int i = 0; i < N; i++) {
            final int j = i;
            System.out.println("士兵" + i + " 报到!");
            allSoldier[i] = new Thread(()->{
                try {
                    // 等待所有士兵,当所有士兵(线程)数量到达N的时候第一次进行回调
                    cyclic.await();
                    try {
                        Thread.sleep(Math.abs(new Random().nextInt() % 10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("士兵" + j + " 任务完成!");
                    // 等待所有士兵完成工作 当士兵(线程)数量到达N的时候进行第二次回调
                    cyclic.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            allSoldier[i].start();
        }
    }
}

/**
 * CyclicBarrier与CountDownLatch的区别
 * countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。CyclicBarrier线程会互相等待
 * 这两个类都可以实现一组线程在到达某个条件之前进行等待，它们内部都有一个计数器，当计数器的值不断的减为0的时候所有阻塞的线程将会被唤醒。
 * 有区别的是CyclicBarrier的计数器由自己控制，而CountDownLatch的计数器则由使用者来控制，
 * 在CyclicBarrier中线程调用await方法不仅会将自己阻塞还会将计数器减1，而在CountDownLatch中线程调用await方法只是将自己阻塞而不会减少计数器的值。
 * CountDownLatch只能拦截一轮，而CyclicBarrier可以实现循环拦截
 * 
 * Phaser和CyclicBarrier类似，但是计数可变	Java7加入的
 */
