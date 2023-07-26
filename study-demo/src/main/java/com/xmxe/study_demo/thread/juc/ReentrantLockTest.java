package com.xmxe.study_demo.thread.juc;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 互斥性：ReentrantLock保证了同一时刻只有一个线程可以持有锁，其他线程必须等待。
 * 可重入性：同一个线程可以多次获取该锁，而不会造成死锁。单线程可以重复进入，但要重复退出
 * 公平性：可以选择公平锁，按照线程请求锁的顺序获取锁，避免线程饥饿。public ReentrantLock(boolean fair),默认锁不公平的,根据线程优先级竞争.
 * 可中断性：提供了可中断的获取锁方式，避免线程在获取锁时长时间阻塞。lock.lockInterruptibly()
 * 条件变量：支持和Condition配合使用，实现复杂的线程通信和同步。
 * 可限时: 超时不能获得锁，就返回false，不会永久等待构成死锁
 * 
 * void lock():获得锁，如果锁被占用则等待。
 * void unlock():释放锁。
 * int getHoldCount()方法的作用是查询当前线程保持此锁定的个数，也就是调用lock()方法的次数。
 * int getQueueLength()方法的作用是返回正等待获取此锁定的线程数。
 * boolean hasQueueThread(Thread thread)的作用是查询指定线程是否正在等待获取此锁定。
 * boolean hasQueueThreads()的作用是查询是否有线程正在等待获取此锁定。
 * isFair()方法的作用是判断是不是公平锁
 * boolean isHeldByCurrentThread()方法的作用是查询当前线程是否保持此锁定
 * Boolean isLocked()方法的作用是查询此锁定是否由任意线程保持。
 * boolean tryLock()方法的作用是仅在调用时锁定未被另一个线程保持的情况下返回true，若锁定以被保持则返回false。并且是立即执行，不会进行等待。tryLock是防止自锁的一个重要方式。
 * boolean tryLock(long timeout,TimeUnit unit)的作用是如果锁定在给定等待时间内没有被另一个线程保持，且当前线程未被中断，则获取该锁定。会在指定时间内等待获取锁。
 * int getWaitQueueLength(Condition con)方法的作用是返回等待与此锁定相关的给定Condition的线程数。就是有多少个指定的Condition实例在等待此锁定。
 * int hasWaiters(Condition con)的作用是查询是否有线程正在等待与此锁定有关的Condition条件。
 * void lockInterruptibly()方法的作用是如果当前线程未被中断则获得锁,如果当前线程被中断则出现异常
 * 
 */
public class ReentrantLockTest implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 10000; j++) {
            lock.lock();
            // 超时设置
            // lock.tryLock(5,TimeUnit.SECONDS);
            try {
                i++;
            } finally {
                // 需要放在finally里释放,如果上面lock了两次,这边也要unlock两次
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockTest tl = new ReentrantLockTest();
        Thread t1 = new Thread(tl);
        Thread t2 = new Thread(tl);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}

/**
 * 如果发生死锁的话中断死锁demo
 * 
 * 线程1,线程2分别去获取lock1,lock2,触发死锁.最终通过DeadlockChecker来触发线程中断
 **/
class DeadLock implements Runnable {

    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();
    int lock;

    public DeadLock(int lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            if (lock == 1) {
                lock1.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                lock2.lockInterruptibly();

            } else {
                lock2.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                lock1.lockInterruptibly();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock1.isHeldByCurrentThread())
                lock1.unlock();
            if (lock2.isHeldByCurrentThread())
                lock2.unlock();
            System.out.println(Thread.currentThread().getId() + "线程中断");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadLock deadLock1 = new DeadLock(1);
        DeadLock deadLock2 = new DeadLock(2);
        // 线程1,线程2分别去获取lock1,lock2.导致死锁
        Thread t1 = new Thread(deadLock1);
        Thread t2 = new Thread(deadLock2);
        t1.start();
        t2.start();
        Thread.sleep(1000);
        // 死锁检查,触发中断
        DeadlockChecker.check();

    }
}

class DeadlockChecker {
    // Java虚拟机线程系统的管理接口
    private final static ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    final static Runnable deadLockCheck = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long[] deadlockedThreadlds = mbean.findDeadlockedThreads();

                if (deadlockedThreadlds != null) {
                    ThreadInfo[] threadInfos = mbean.getThreadInfo(deadlockedThreadlds);
                    for (Thread t : Thread.getAllStackTraces().keySet()) {
                        for (int i = 0; i < threadInfos.length; i++) {
                            if (t.getId() == threadInfos[i].getThreadId()) {
                                t.interrupt();
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    public static void check() {
        Thread t = new Thread(deadLockCheck);
        t.setDaemon(true);
        t.start();
    }
}