package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有图解有案例，我终于把Condition的原理讲透彻了-https://mp.weixin.qq.com/s/5-wPUOQol_OIeCIBEpF7kg
 * Condition是个接口，基本的方法就是await()和signal()方法；
 * Condition依赖于Lock接口，生成一个Condition的基本代码是lock.newCondition()
 * 调用Condition的await()和signal()方法，都必须在lock保护之内，就是说必须在lock.lock()和lock.unlock之间才可以使用
 * 类似于 Object.wait()和Object.notify(), 需要与ReentrantLock结合使用.
 * 
 * await()方法会使当前线程等待，同时释放当前锁，这和Object.wait()方法很相似,当其他线程中使用signal()时或者signalAll()方法时，线程会重新获得锁并继续执行。或者当线程被中断时，也能跳出等待。
 * void await() throws InterruptedException; 造成当前线程在接到信号或被中断之前一直处于等待状态。
 * void awaitUninterruptibly(); 方法与await()方法基本相同，造成当前线程在接到信号之前一直处于等待状态。但是它并不会再等待过程中响应中断。 【该方法对中断不敏感】
 * long awaitNanos(long nanosTimeout) throws InterruptedException; 造成当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态。返回值表示剩余时间，如果在nanosTimesout之前唤醒，那么返回值 = nanosTimeout - 消耗时间，如果返回值 <= 0 ,则可以认定它已经超时了。
 * boolean await(long time, TimeUnit unit) throws InterruptedException; 造成当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态
 * boolean awaitUntil(Date deadline) throws InterruptedException;造成当前线程在接到信号、被中断或到达指定最后期限之前一直处于等待状态。如果没有到指定时间就被通知，则返回true，否则表示到了指定时间，返回返回false
 * 
 * singal()方法用于唤醒一个在等待中的线程。相对的singalAll()方法会唤醒所有在等待中的线程。这和Obejct.notify()方法很类似。 
 * void signal(); 唤醒一个等待线程。该线程从等待方法返回前必须获得与Condition相关的锁。
 * void signalAll();唤醒所有等待线程。能够从等待方法返回的线程必须获得与Condition相关的锁。
 */
public class ReenterLockCondition implements Runnable{

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    @Override
    public void run() {
        try {
            lock.lock();
            condition.await();
            System.out.println("Thread is going on");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 注意放到finally中释放
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockCondition t1 = new ReenterLockCondition();
        Thread tt = new Thread(t1);
        tt.start();
        Thread.sleep(2000);
        System.out.println("after sleep, signal!");
        // 通知线程tt继续执行. 唤醒同样需要重新获得锁
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
