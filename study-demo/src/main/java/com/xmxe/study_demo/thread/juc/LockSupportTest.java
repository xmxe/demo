package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport是Java并发包中的一个工具类，用于线程的阻塞和唤醒操作。它提供了一种基于线程的阻塞机制，可以用来实现类似于wait()和notify()的线程间通信，并且比传统的wait()和notify()更加灵活和安全。
 * 与wait()和notify()不同，LockSupport不需要获取对象的锁即可对线程进行阻塞和唤醒操作。这使得它更加灵活，可以在任何地方使用，而不限定于在同步代码块或同步方法中使用。LockSupport是以线程为单位进行操作的，即每个线程都有自己的许可（permit）计数器，调用park()方法会使线程阻塞，而调用unpark(Thread thread)方法会给指定线程一个许可，使其恢复执行。
 * LockSupport内部维护了一个许可许可（permit）计数器，每次调用unpark(Thread thread)方法都会增加目标线程的许可计数器，让该线程能够获得许可，即使在调用park()方法之前也能够立即恢复执行。
 * LockSupport底层实现依赖Unsafe，同时它还是锁和其他同步类实现的基础，LockSupport提供两类静态函数分别是park和unpark，即阻塞与唤醒线程
 * 
 * public static void park(Object blocker); // 暂停当前线程
 * public static void parkNanos(Object blocker, long nanos); // 暂停当前线程，不过有超时时间的限制
 * public static void parkUntil(Object blocker, long deadline); // 暂停当前线程，直到某个时间
 * public static void park(); // 使当前线程进入等待状态（阻塞）
 * public static void parkNanos(long nanos); // 暂停当前线程，不过有超时时间的限制
 * public static void parkUntil(long deadline); // 暂停当前线程，直到某个时间
 * public static void unpark(Thread thread); // 解除线程的阻塞状态并恢复执行
 * public static Object getBlocker(Thread t);
 * 
 */
public class LockSupportTest {
    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    static class ChangeObjectThread extends Thread {
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
        t1.join();// 哪个线程调用join哪个线程优先执行,线程等待另一个调用join()方法的线程执行结束后再往下执行
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

        @Override
        public void run() {
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

class LockSupportDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = Thread.currentThread();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000); // 模拟耗时操作
                System.out.println("子线程执行完成，唤醒主线程");
                LockSupport.unpark(mainThread); // 唤醒主线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        System.out.println("主线程阻塞");
        LockSupport.park(); // 主线程阻塞

        System.out.println("主线程恢复执行");
    }
}