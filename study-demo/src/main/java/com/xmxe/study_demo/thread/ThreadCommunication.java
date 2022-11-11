package com.xmxe.study_demo.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程通信的5中方式 https://mp.weixin.qq.com/s/47UlDrzbH9cKeQ1g3DHQeQ
 */
public class ThreadCommunication {
    
    /**
     * 1. 使用 volatile 关键字
     */
    static class VolatileSync {
        //定义共享变量来实现通信，它需要volatile修饰，否则线程不能及时感知
        static volatile boolean notice = false;
    
        public static void main(String[] args) {
            List<String>  list = new ArrayList<>();
            //线程A
            Thread threadA = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素，此时list的size为：" + list.size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list.size() == 5)
                        notice = true;
                }
            });
            //线程B
            Thread threadB = new Thread(() -> {
                while (true) {
                    if (notice) {
                        System.out.println("线程B收到通知，开始执行自己的业务...");
                        break;
                    }
                }
            });
            //需要先启动线程B
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
     * 2. 使用 Object 类的 wait()/notify()
     * wait/notify 必须配合 synchronized 使用，wait 方法释放锁，notify 方法不释放锁。wait 是指在一个已经进入了同步锁的线程内，
     * 让自己暂时让出同步锁，以便其他正在等待此锁的线程可以得到同步锁并运行，只有其他线程调用了notify()，notify并不释放锁，只是
     * 告诉调用过wait()的线程可以去参与获得锁的竞争了，但不是马上得到锁，因为锁还在别人手里，别人还没释放，调用 wait() 的一个
     * 或多个线程就会解除 wait 状态，重新参与竞争对象锁，程序如果可以再次得到锁，就可以继续向下运行
     */
    static class ObjectSync {
        public static void main(String[] args) {
            //定义一个锁对象
            Object lock = new Object();
            List<String>  list = new ArrayList<>();
            // 线程A
            Thread threadA = new Thread(() -> {
                synchronized (lock) {
                    for (int i = 1; i <= 10; i++) {
                        list.add("abc");
                        System.out.println("线程A添加元素，此时list的size为：" + list.size());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (list.size() == 5)
                            lock.notify();//唤醒B线程
                    }
                }
            });
            //线程B
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
                        System.out.println("线程B收到通知，开始执行自己的业务...");
                    }
                }
            });
            //需要先启动线程B
            threadB.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //再启动线程A
            threadA.start();
        }
    }

    /**
     * 3. 使用JUC工具类 CountDownLatch
     */
    static class CountDownLatchSync {
        public static void main(String[] args) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            List<String>  list = new ArrayList<>();
            //线程A
            Thread threadA = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素，此时list的size为：" + list.size());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (list.size() == 5)
                        countDownLatch.countDown();
                }
            });
            //线程B
            Thread threadB = new Thread(() -> {
                while (true) {
                    if (list.size() != 5) {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("线程B收到通知，开始执行自己的业务...");
                    break;
                }
            });
            //需要先启动线程B
            threadB.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //再启动线程A
            threadA.start();
        }
    }

    /**
     * 4. 使用 ReentrantLock 结合 Condition
     */
    static class ReentrantLockSync {
        public static void main(String[] args) {
            ReentrantLock lock = new ReentrantLock();
            Condition condition = lock.newCondition();
    
            List<String> list = new ArrayList<>();
            //线程A
            Thread threadA = new Thread(() -> {
                lock.lock();
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素，此时list的size为：" + list.size());
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
            //线程B
            Thread threadB = new Thread(() -> {
                lock.lock();
                if (list.size() != 5) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("线程B收到通知，开始执行自己的业务...");
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
     * 5. 基本 LockSupport 实现线程间的阻塞和唤醒
     * LockSupport 是一种非常灵活的实现线程间阻塞和唤醒的工具，使用它不用关注是等待线程先进行还是唤醒线程先运行，但是得知道线程的名字
     */
    static class TestSync {
        public static void main(String[] args) {
            List<String> list = new ArrayList<>();
            //线程B
            final Thread threadB = new Thread(() -> {
                if (list.size() != 5) {
                    LockSupport.park();
                }
                System.out.println("线程B收到通知，开始执行自己的业务...");
            });
            //线程A
            Thread threadA = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    list.add("abc");
                    System.out.println("线程A添加元素，此时list的size为：" + list.size());
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
