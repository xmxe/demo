package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Exchanger可用于两个线程之间交换信息.适用场景:当两个线程工作在同一个类的不同实例上时,用于交换数据。
 * 它提供一个同步点,在这个同步点两个线程可以交换彼此的数据。这两个线程通过exchange方法交换数据,如果第一个线程先执行exchange方法,它会一直等待第二个线程也执行exchange,当两个线程都到达同步点时,这两个线程就可以交换数据,将本线程生产出来的数据传递给对方。
 * 因此使用Exchanger的重点是成对的线程使用exchange()方法,当有一对线程达到了同步点,就会进行交换数据。因此该工具类的线程对象是成对的。
 * 
 * String exchange(V x):用于交换,启动交换并等待另一个线程调用exchange；线程一直阻塞,直到其他任意线程和它交换数据,或者被线程中断
 * String exchange(V x,long timeout,TimeUnit unit):用于交换,启动交换并等待另一个线程调用exchange,并且设置最大等待时间,当等待时间超过timeout便停止等待。
 */
public class ExchangerTest {
    public static void main(String[] args) throws InterruptedException {
        demo1();
        demo2();
        test1();
        test2();
        test3();
        test4();
    }

    public static void demo1() throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
        new Producer("", exchanger).start();
        new Consumer("", exchanger).start();
        TimeUnit.SECONDS.sleep(7);
        System.exit(-1);
    }

    public static void demo2() {
        ExecutorService executor = Executors.newCachedThreadPool();

        final Exchanger<String> exchanger = new Exchanger<>();
        executor.execute(new Runnable() {
            String data1 = "克拉克森,小拉里南斯";

            @Override
            public void run() {
              nbaTrade(data1, exchanger);
            }
        });

        executor.execute(new Runnable() {
            String data1 = "格里芬";

            @Override
            public void run() {
              nbaTrade(data1, exchanger);
            }
        });

        executor.execute(new Runnable() {
            String data1 = "哈里斯";

            @Override
            public void run() {
              nbaTrade(data1, exchanger);
            }
        });

        executor.execute(new Runnable() {
            String data1 = "以赛亚托马斯,弗莱";

            @Override
            public void run() {
              nbaTrade(data1, exchanger);
            }
        });

        executor.shutdown();
    }

    private static void nbaTrade(String data1, Exchanger<String> exchanger) {
        try {
            System.out.println(Thread.currentThread().getName() + "在交易截止之前把 " + data1 + " 交易出去");
            Thread.sleep((long) (Math.random() * 1000));

            String data2 = (String) exchanger.exchange(data1);
            System.out.println(Thread.currentThread().getName() + "交易得到" + data2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、创建并启动两个线程；
     * 2、进行数据交换前先打印出自己线程的数据；
     * 3、进行数据交换；
     * 4、打印数据交换之后的数据；
     */
    private static void test1() {
        Exchanger<Object> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                Object data = "-公众号Java技术栈AAA";
                System.out.println(Thread.currentThread().getName() + data);

                // 开始交换数据
                data = exchanger.exchange(data);
                System.out.println(Thread.currentThread().getName() + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Object data = "-公众号Java技术栈BBB";
                System.out.println(Thread.currentThread().getName() + data);

                // 开始交换数据
                data = exchanger.exchange(data);
                System.out.println(Thread.currentThread().getName() + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 现在只启动了一个线程,并且设置了超时时间3秒。
     */
    private static void test2() {
        Exchanger<Object> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                Object data = "-公众号Java技术栈AAA";
                System.out.println(Thread.currentThread().getName() + data);

                // 开始交换数据
                data = exchanger.exchange(data, 3000L, TimeUnit.MILLISECONDS);
                System.out.println(Thread.currentThread().getName() + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 中断数据交换
     * 线程开始交换数据后,会一直阻塞直到其他任意线程和它交换数据,或者被中断、超时,上面演示了超时,下面这个示例演示一下中断
     */
    private static void test3() {
        Exchanger<Object> exchanger = new Exchanger<>();

        Thread thread = new Thread(() -> {
            try {
              Object data = "-公众号Java技术栈AAA";
              System.out.println(Thread.currentThread().getName() + data);

              // 开始交换数据
              data = exchanger.exchange(data);
              System.out.println(Thread.currentThread().getName() + data);
            } catch (Exception e) {
              e.printStackTrace();
            }
        });

        thread.start();
        // 线程中断
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

    /**
     * 开启10个线程,看它们是怎么两两交换的
     */
    private static void test4() {
        Exchanger<Object> exchanger = new Exchanger<>();

        for (int i = 1; i <= 10; i++) {
            Integer data = i;
            new Thread(() -> {
              try {
                  Object exchange = exchanger.exchange(data);
                  System.out.println(Thread.currentThread().getName() + "-" + exchange);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
            }, "Java技术栈" + i).start();
        }
    }

    static class Producer extends Thread {
        private Exchanger<String> exchanger;
        private static String data = "0";

        Producer(String name, Exchanger<String> exchanger) {
            super("Producer-" + name);
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 1; i < 5; i++) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    data = String.valueOf(i);
                    System.out.println(getName() + " 交换前:" + data);
                    data = exchanger.exchange(data);
                    System.out.println(getName() + " 交换后:" + data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer extends Thread {
        private Exchanger<String> exchanger;
        private static String data = "0";

        Consumer(String name, Exchanger<String> exchanger) {
            super("Consumer-" + name);
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            while (true) {
                data = "0";
                System.out.println(getName() + " 交换前:" + data);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    data = exchanger.exchange(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                  System.out.println(getName() + " 交换后:" + data);
            }
        }
    }

}