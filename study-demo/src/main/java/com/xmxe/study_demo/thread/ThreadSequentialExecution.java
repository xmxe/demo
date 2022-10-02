package com.xmxe.study_demo.thread;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程按序打印ABC？十二种做法，深入多线程同步通信机制！https:// mp.weixin.qq.com/s/8xyx_IfNTpSOLN1XAroSIA
 * 
 */
public class ThreadSequentialExecution {
    
    /**
     * 1. synchronized+wait+notify
     */
    public void synchronizedWaitNotify(){
        class ABC1 {
            // 锁住的对象
            private final static Object lock = new Object();
            // A是否已经执行
            private static boolean aExecuted = false;
            // B是否已经执行过
            private static boolean bExecuted = false;
        
            public static void printA() {
                synchronized (lock) {
                    System.out.println("A");
                    aExecuted = true;
                    // 唤醒所有等待线程
                    lock.notifyAll();
                }
            }
        
            public static void printB() throws InterruptedException {
                synchronized (lock) {
                    // 获取到锁，但是要等A执行
                    while (!aExecuted) {
                        lock.wait();
                    }
                    System.out.println("B");
                    bExecuted = true;
                    lock.notifyAll();
                }
            }
        
            public static void printC() throws InterruptedException {
                synchronized (lock) {
                    // 获取到锁，但是要等B执行
                    while (!bExecuted) {
                        lock.wait();
                    }
                    System.out.println("C");
                }
            }

            public static void test(){
                // 线程A
                new Thread(() -> {
                    ABC1.printA();
                }, "A").start();
                // 线程B
                new Thread(() -> {
                    try {
                        ABC1.printB();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "B").start();
                // 线程C
                new Thread(() -> {
                    try {
                        ABC1.printC();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "C").start();
            }
        
        }
        
    }


    /**
     * 2. lock+全局变量state
     */
    public void lockState() {
        class ABC2 {
            // 可重入锁
            private final static Lock lock = new ReentrantLock();
            // 判断是否执行：1表示应该A执行，2表示应该B执行，3表示应该C执行
            private static int state = 1;
        
            public static void printA() {
                // 自旋
                while (state < 4) {
                    try {
                        // 获取锁
                        lock.lock();
                        // 并发情况下，不能用if，要用循环判断等待条件，避免虚假唤醒
                        while (state == 1) {
                            System.out.println("A");
                            state++;
                        }
                    } finally {
                        // 要保证不执行的时候，锁能释放掉
                        lock.unlock();
                    }
                }
            }
        
            public static void printB() {
                while (state < 4) {
                    try {
                        lock.lock();
                        // 获取到锁，应该执行
                        while (state == 2) {
                            System.out.println("B");
                            state++;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        
            public static void printC() {
                while (state < 4) {
                    try {
                        lock.lock();
                        while (state == 3) {
                            // 获取到锁，应该执行
                            System.out.println("C");
                            state++;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        
        }
    }


    /**
     * 3. volatile
     */
    public void volatileWord() {
        class ABC3 {

            // 判断是否执行：1表示应该A执行，2表示应该B执行，3表示应该C执行
            private static volatile Integer state = 1;
        
            public static void printA() {
                // 通过循环，hang住线程
                while (state != 1) {
                }
                System.out.println("A");
                state++;
            }
        
            public static void printB() {
                while (state != 2) {
                }
                System.out.println("B");
                state++;
            }
        
            public static void printC() {
                while (state != 3) {
                }
                System.out.println("C");
                state++;
            }
        
        }
    }


    /**
     * 4. AtomicInteger
     */
    public void atomicInteger(){
        class ABC4 {

            // 判断是否执行：1表示应该A执行，2表示应该B执行，3表示应该C执行
            private static AtomicInteger state = new AtomicInteger(1);
        
            public static void printA() {
                System.out.println("A");
                state.incrementAndGet();
            }
        
            public static void printB() {
                while (state.get() < 4) {
                    while (state.get() == 2) {
                        System.out.println("B");
                        state.incrementAndGet();
                    }
                }
            }
        
            public static void printC() {
                while (state.get() < 4) {
                    while (state.get() == 3) {
                        System.out.println("C");
                        state.incrementAndGet();
                    }
                }
            }
        
        }
    }


    /**
     * 5.lock+condition
     */
    public void lockCondition(){
        class ABC5 {
            // 可重入锁
            private final static Lock lock = new ReentrantLock();
            // 判断是否执行：1表示应该A执行，2表示应该B执行，3表示应该C执行
            private static int state = 1;
            // condition对象
            private static Condition a = lock.newCondition();
            private static Condition b = lock.newCondition();
            private static Condition c = lock.newCondition();
        
            public static void printA() {
                // 通过循环，hang住线程
                while (state < 4) {
                    try {
                        // 获取锁
                        lock.lock();
                        // 并发情况下，不能用if，要用循环判断等待条件，避免虚假唤醒
                        while (state != 1) {
                            a.await();
                        }
                        System.out.println("A");
                        state++;
                        b.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        // 要保证不执行的时候，锁能释放掉
                        lock.unlock();
                    }
                }
            }
        
            public static void printB() throws InterruptedException {
                while (state < 4) {
                    try {
                        lock.lock();
                        // 获取到锁，应该执行
                        while (state != 2) {
                            b.await();
                        }
                        System.out.println("B");
                        state++;
                        c.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        
            public static void printC() throws InterruptedException {
                while (state < 4) {
                    try {
                        lock.lock();
                        while (state != 3) {
                            c.await();
                        }
                        // 获取到锁，应该执行
                        System.out.println("C");
                        state++;
                    } finally {
                        lock.unlock();
                    }
                }
            }
        
        }
    }


    /**
     * 6.信号量Semaphore
     */
    public void semaphore(){
        class ABC6 {

            private static Semaphore semaphoreB = new Semaphore(0);
            private static Semaphore semaphoreC = new Semaphore(0);
        
            public static void printA() {
                System.out.println("A");
                semaphoreB.release();
            }
        
            public static void printB() throws InterruptedException {
                semaphoreB.acquire();
                System.out.println("B");
                semaphoreC.release();
            }
        
            public static void printC() throws InterruptedException {
                semaphoreC.acquire();
                System.out.println("C");
            }
        
        }
    }


    /**
     * 7.计数器CountDownLatch
     */
    public void countDownLatch(){
        class ABC7 {

            private static CountDownLatch countDownLatchB = new CountDownLatch(1);
            private static CountDownLatch countDownLatchC = new CountDownLatch(1);
        
            public static void printA() {
                System.out.println("A");
                countDownLatchB.countDown();
            }
        
            public static void printB() throws InterruptedException {
                countDownLatchB.await();
                System.out.println("B");
                countDownLatchC.countDown();
            }
        
            public static void printC() throws InterruptedException {
                countDownLatchC.await();
                System.out.println("C");
            }
        
        }
    }


    /**
     * 8. 循环栅栏CyclicBarrier
     */
    public void cyclicBarrier() {
        class ABC8 {

            private static CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
            private static Integer state = 1;
        
            public static void printA() {
                while (state != 1) {
                }
                System.out.println("A");
                state = 2;
            }
        
            public static void printB() {
                try {
                    // 在栅栏前等待
                    cyclicBarrier.await();
                    // state不等于2的时候等待
                    while (state != 2) {
                    }
                    System.out.println("B");
                    state = 3;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
            public static void printC() {
                try {
                    cyclicBarrier.await();
                    while (state != 3) {
                    }
                    System.out.println("C");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
        }
        
    }

    /**
     * 9.交换器Exchanger
     */
    public void exchanger(){
        class ABC9 {
            private static Exchanger<Integer> exchangerB = new Exchanger<>();
            private static Exchanger<Integer> exchangerC = new Exchanger<>();
        
            public static void printA() {
                System.out.println("A");
                try {
                    // 交换
                    exchangerB.exchange(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        
            public static void printB() {
                try {
                    // 交换
                    Integer state = exchangerB.exchange(0);
                    // 等待
                    while (state != 2) {
                    }
                    // 执行
                    System.out.println("B");
                    // 第二次交换
                    exchangerC.exchange(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        
            public static void printC() {
                try {
                    Integer state = exchangerC.exchange(0);
                    while (state != 3) {
                    }
                    System.out.println("C");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }


    /**
     * 10.ThreadLocal
     */
    public void threadLocal() {
        class ABC10 {
    
            public static void main(String[] args) {
                // 使用ThreadLocal存储变量
                ThreadLocal<Integer> threadLocal = new InheritableThreadLocal<>();
                threadLocal.set(1);
                new Thread(() -> {
                    System.out.println("A");
                }, "A").start();
                // 设置变量值
                threadLocal.set(2);
        
                new Thread(() -> {
                    // 等待
                    while (threadLocal.get() != 2) {
                    }
                    System.out.println("B");
                }, "B").start();
                threadLocal.set(3);
        
                new Thread(() -> {
                    while (threadLocal.get() != 3) {
                    }
                    System.out.println("C");
                }, "C").start();
            }
        }
    }

    /**
     * 11.管道流PipedStream
     */
    public void pipedStream(){
        class ABC11 {
            public static void main(String[] args) throws Exception {
                // 线程A的输出流
                PipedOutputStream outputStreamA = new PipedOutputStream();
                // 线程B的输出流
                PipedOutputStream outputStreamB = new PipedOutputStream();
                // 线程B的输入流
                PipedInputStream inputStreamB = new PipedInputStream();
                // 线程C的输入流
                PipedInputStream inputStreamC = new PipedInputStream();
        
        
                outputStreamA.connect(inputStreamB);
                outputStreamB.connect(inputStreamC);
        
                new Thread(() -> {
                    System.out.println("A");
                    try {
                        // 流写入
                        outputStreamA.write("B".getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "A").start();
        
                new Thread(() -> {
                    // 流读取
                    byte[] buffer = new byte[1];
                    try {
                        inputStreamB.read(buffer);
                        // 转换成String
                        String msg = new String(buffer);
                        System.out.println(msg);
                        outputStreamB.write("C".getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "B").start();
        
                new Thread(() -> {
                    byte[] buffer = new byte[1];
                    try {
                        inputStreamC.read(buffer);
                        String msg = new String(buffer);
                        System.out.println(msg);
                    } catch (   Exception e) {
                        e.printStackTrace();
                    }
                }, "C").start();
            }
        }
    }


    /**
     * 12.阻塞队列BlockingQueue
     */ 
    public void blockQueue(){
        class ABC12 {

            private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
        
            public static void printA() {
                System.out.println("A");
                queue.offer("B");
            }
        
            public static void printB() {
                while (queue.size() != 1) {
                }
                System.out.println("B");
                queue.offer("C");
            }
        
            public static void printC()  {
                while (queue.size() != 2) {
                }
                System.out.println("C");
            }
        
        }
    }
}
