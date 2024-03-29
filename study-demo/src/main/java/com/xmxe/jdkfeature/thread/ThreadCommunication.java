package com.xmxe.jdkfeature.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程间通信的模型有两种：共享内存和消息传递
 * 有两个线程,A线程向一个集合里面依次添加元素“abc”字符串,一共添加十次,当添加到第五次的时候,希望B线程能够收到A线程的通知,然后B线程执行相关的业务操作。
 */
public class ThreadCommunication {

    /**
     * 1.使用volatile关键字
     * 基于volatile关键字来实现线程间相互通信是使用共享内存的思想。大致意思就是多个线程同时监听一个变量,当这个变量发生变化的时候,线程能够感知并执行相应的业务。这也是最简单的一种实现方式
     */
    static class VolatileSync {
        // 定义共享变量来实现通信,它需要volatile修饰,否则线程不能及时感知
        static volatile boolean notice = false;

        public static void main(String[] args) {
            List<String> list = new ArrayList<>();
            // 线程A
            Thread threadA = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素,此时list的size为：" + list.size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list.size() == 5)
                        notice = true;
                }
            });
            // 线程B
            Thread threadB = new Thread(() -> {
                while (true) {
                    if (notice) {
                        System.out.println("线程B收到通知,开始执行自己的业务...");
                        break;
                    }
                }
            });
            // 需要先启动线程B
            threadB.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 再启动线程A
            threadA.start();
        }
    }

    /**
     * 2.使用Object类的wait()/notify()
     * wait/notify必须配合synchronized使用,wait方法释放锁,notify方法不释放锁。wait是指在一个已经进入了同步锁的线程内,
     * 让自己暂时让出同步锁,以便其他正在等待此锁的线程可以得到同步锁并运行,只有其他线程调用了notify(),notify并不释放锁,只是
     * 告诉调用过wait()的线程可以去参与获得锁的竞争了,但不是马上得到锁,因为锁还在别人手里,别人还没释放,调用wait()的一个
     * 或多个线程就会解除wait状态,重新参与竞争对象锁,程序如果可以再次得到锁,就可以继续向下运行
     */
    static class ObjectSync {
        public static void main(String[] args) {
            // 定义一个锁对象
            Object lock = new Object();
            List<String> list = new ArrayList<>();
            // 线程A
            Thread threadA = new Thread(() -> {
                synchronized (lock) {
                    for (int i = 1; i <= 10; i++) {
                        list.add("abc");
                        System.out.println("线程A添加元素,此时list的size为：" + list.size());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (list.size() == 5)
                            lock.notify();// 唤醒B线程
                    }
                }
            });
            // 线程B
            Thread threadB = new Thread(() -> {
                while (true) {
                    synchronized (lock) {
                        if (list.size() != 5) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("线程B收到通知,开始执行自己的业务...");
                    }
                }
            });
            // 需要先启动线程B
            threadB.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 再启动线程A
            threadA.start();
        }
    }

    /**
     * 3.使用JUC工具类CountDownLatch
     */
    static class CountDownLatchSync {
        public static void main(String[] args) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            List<String> list = new ArrayList<>();
            // 线程A
            Thread threadA = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素,此时list的size为：" + list.size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list.size() == 5)
                        countDownLatch.countDown();
                }
            });
            // 线程B
            Thread threadB = new Thread(() -> {
                while (true) {
                    if (list.size() != 5) {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("线程B收到通知,开始执行自己的业务...");
                    break;
                }
            });
            // 需要先启动线程B
            threadB.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 再启动线程A
            threadA.start();
        }
    }

    /**
     * 4.使用ReentrantLock结合Condition
     */
    static class ReentrantLockSync {
        public static void main(String[] args) {
            ReentrantLock lock = new ReentrantLock();
            Condition condition = lock.newCondition();

            List<String> list = new ArrayList<>();
            // 线程A
            Thread threadA = new Thread(() -> {
                lock.lock();
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素,此时list的size为：" + list.size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list.size() == 5)
                        condition.signal();
                }
                lock.unlock();
            });
            // 线程B
            Thread threadB = new Thread(() -> {
                lock.lock();
                if (list.size() != 5) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("线程B收到通知,开始执行自己的业务...");
                lock.unlock();
            });
            threadB.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadA.start();
        }
    }

    /**
     * 5.基本LockSupport实现线程间的阻塞和唤醒
     * LockSupport是一种非常灵活的实现线程间阻塞和唤醒的工具,使用它不用关注是等待线程先进行还是唤醒线程先运行,但是得知道线程的名字
     */
    static class TestSync {
        public static void main(String[] args) {
            List<String> list = new ArrayList<>();
            // 线程B
            final Thread threadB = new Thread(() -> {
                if (list.size() != 5) {
                    LockSupport.park();
                }
                System.out.println("线程B收到通知,开始执行自己的业务...");
            });
            // 线程A
            Thread threadA = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素,此时list的size为：" + list.size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list.size() == 5)
                        LockSupport.unpark(threadB);
                }
            });
            threadA.start();
            threadB.start();
        }
    }

}
