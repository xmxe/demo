package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 线程工具类有啥用？(https://mp.weixin.qq.com/s/kq5pLJA2xm51lIcuE-pWAQ)
 * 
 * LockSupport:一个线程阻塞工具, 可以在任意位置让线程阻塞.与suspend()比较, 如果unpark发生在park之前, 并不会导致线程冻结, 也不需要获取锁.
 * 
 * public static void park(Object blocker); // 暂停当前线程
 * public static void parkNanos(Object blocker, long nanos); // 暂停当前线程，不过有超时时间的限制
 * public static void parkUntil(Object blocker, long deadline); // 暂停当前线程，直到某个时间
 * public static void park(); // 无期限暂停当前线程
 * public static void parkNanos(long nanos); // 暂停当前线程，不过有超时时间的限制
 * public static void parkUntil(long deadline); // 暂停当前线程，直到某个时间
 * public static void unpark(Thread thread); // 恢复当前线程
 * public static Object getBlocker(Thread t);
 * 
 */
public class LockSupportTest {
    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    static ChangeObjectThread t2 = new ChangeObjectThread("t2");
    public static class ChangeObjectThread extends Thread {

        public ChangeObjectThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            // synchronized (u) {
                System.out.println("in " + getName());
                LockSupport.park();
                System.out.println("out " + getName());
            // }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        t1.start();
        Thread.sleep(100);
        t2.start();
        LockSupport.unpark(t1);
        t1.join();// 哪个线程调用join哪个线程优先执行 线程等待另一个调用join()方法的线程执行结束后再往下执行
        LockSupport.unpark(t2);
        t2.join();
    }
}
/**
 * 相对于线程的stop和resume，park和unpark的先后顺序并不是那么严格。stop和resume如果顺序反了，会出现死锁现象。而park和unpark却不会,如下demo
 * t1内部有休眠1s的操作，所以unpark肯定先于park的调用，但是t1最终仍然可以完结。这是因为park和unpark会对每个线程维持一个许可（boolean值）
 * unpark调用时，如果当前线程还未进入park，则许可为true,park调用时，判断许可是否为true，如果是true，则继续往下执行；如果是false，则等待，直到许可为true
 * 
 */ 

class LockSupportDemo {

    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");

    public static class ChangeObjectThread extends Thread {

        public ChangeObjectThread(String name) {
            super(name);
        }

        @Override public void run() {
            synchronized (u) {
                System.out.println("in " + getName());
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LockSupport.park();
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("被中断了");
                }
                System.out.println("继续执行");
            }
        }
    }

    public static void main(String[] args) {
        t1.start();
        LockSupport.unpark(t1);
        System.out.println("unpark invoked");
    }
}