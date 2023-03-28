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
 * 三个线程按序打印ABC？十二种做法
 * 
 */
public class ThreadSequentialExecution {

    /**
     * 1. synchronized+wait+notify
     * 思路:打印的时候需要获取锁,打印B的线程需要等待打印A线程执行完，打印C的线程需要等待打印B线程执行完
     */
    public void synchronizedWaitNotify() {
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

            public static void test() {
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
     * 思路:用lock来实现同步,用全局变量state标识改哪个线程执行，不执行就释放锁
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
        /*
         * 这里也有几个细节要注意：
         * 要在循环里获取锁，不然线程可能会在获取到锁之前就终止了
         * 要用while，而不是if判断，是否当前线程应该打印输出
         * 要在finally里释放锁，保证其它的线程能获取到锁
         */
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
    public void atomicInteger() {
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
     * 在Java中，除了Object的wait和notify/notify可以实现等待/通知机制，Condition和Lock配合同样可以完成等待通知机制。
     * 使用condition.await()，使当前线程进入等待状态，使用condition.signal()或者condition.signalAll()唤醒等待线程。
     */
    public void lockCondition() {
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
     * 使用acquire()获取许可，如果没有可用的许可，线程进入阻塞等待状态；使用release释放许可。
     */
    public void semaphore() {
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
     * CountDownLatch的一个适用场景，就是用来进行多个线程的同步管理，线程调用了countDownLatch.await()之后，需要等待countDownLatch的信号countDownLatch.countDown()，在收到信号前，它不会往下执行。
     */
    public void countDownLatch() {
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
     * 线程B和线程C需要使用栅栏等待,为了让B和C也顺序执行，需要用一个状态，来标识应该执行的线程
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
        /*
         * 当然，CyclicBarrier的实现其实还是基于lock+condition，多个线程在到达一定条件前await，到达条件后signalAll
         */

    }

    /**
     * 9.交换器Exchanger
     * 线程A执行完之后，和线程B用一个交换器交换state，线程B执行完之后，和线程C用一个交换器交换state,在没有轮到自己执行之前，先进行等待
     */
    public void exchanger() {
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
     * 线程之间通信，还有一种比较笨重的办法——PipedInputStream/PipedOutStream。一个线程使用PipedOutStream写数据，一个线程使用PipedInputStream读数据，而且Piped的读取只能一对一。
     * 那么，在这道题里：
     * 线程A使用PipedOutStream向线程B写入数据，线程B读取后，打印输出
     * 线程B和C也是相同的姿势
     */
    public void pipedStream() {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "C").start();
            }
        }
    }

    /**
     * 12.阻塞队列BlockingQueue
     * 阻塞队列同样也可以用来进行线程调度。
     * 利用队列的长度，来确定执行者
     * 利用队列的阻塞性，来保证入队操作同步执行。
     */
    public void blockQueue() {
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

            public static void printC() {
                while (queue.size() != 2) {
                }
                System.out.println("C");
            }

        }
    }
}
