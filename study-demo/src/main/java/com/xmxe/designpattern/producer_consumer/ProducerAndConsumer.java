package com.xmxe.designpattern.producer_consumer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者和消费者模型
 * 生产者和消费者模型作为一个非常重要的设计模型，它的优点在于：
 * 解耦：生产者和消费者之间不直接进行交互，即使生产者和消费者的代码发生变化，也不会对对方产生影响
 * 消峰：例如在某项工作中，假如A操作生产数据的速度很快，B操作处理速度很慢，那么A操作就必须等待B操作完成才能结束，反之亦然。如果将A操作和B操作进行解耦，中间插入一个缓冲区，这样A操作将生产的数据存入缓冲区，就接受了.B操作从缓冲区获取数据并进行处理，平衡好A操作和B操作之间的缓冲区，可以显著提升系统的数据处理能力
 */
public class ProducerAndConsumer {
    
    /**
     * 利用wait / notify方法实现思路
     */
    /**
     * 缓冲区容器类
     */
    static class Container {

        /**
         * 缓冲区最大容量
         */
        private int capacity = 3;

        /**
         * 缓冲区
         */
        private LinkedList<Integer> list = new LinkedList<Integer>();


        /**
         * 添加数据到缓冲区
         * @param value
         */
        public synchronized void add(Integer value) {
            if(list.size() >= capacity){
                System.out.println("生产者："+ Thread.currentThread().getName()+"，缓冲区已满,生产者进入waiting...");
                try {
                    // 进入等待状态
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("生产者："+ Thread.currentThread().getName()+"，add：" + value);
            list.add(value);

            //唤醒其他所有处于wait()的线程，包括消费者和生产者
            notifyAll();
        }


        /**
         * 从缓冲区获取数据
         */
        public synchronized void get() {
            if(list.size() == 0){
                System.out.println("消费者："+ Thread.currentThread().getName()+"，缓冲区为空,消费者进入waiting...");
                try {
                    // 进入等待状态
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从头部获取数据，并移除元素
            Integer val = list.removeFirst();
            System.out.println("消费者："+ Thread.currentThread().getName()+"，value：" + val);

            //唤醒其他所有处于wait()的线程，包括消费者和生产者
            notifyAll();
        }
    }
    /**
     * 生产者类
     */
    static class Producer extends Thread{

        private Container container;

        public Producer(Container container) {
            this.container = container;
        }

        @Override
        public void run() {
            for (int i = 0; i < 6; i++) {
                container.add(i);
            }
        }
    }

    /**
     * 消费者类
     */
    static class Consumer extends Thread{

        private Container container;

        public Consumer(Container container) {
            this.container = container;
        }

        @Override
        public void run() {
            for (int i = 0; i < 6; i++) {
                container.get();
            }
        }
    }

    /**
     * 测试类
     */
    static class MyThreadTest {

        public static void main(String[] args) {
            Container container = new Container();
            Producer producer = new Producer(container);
            Consumer consumer = new Consumer(container);

            producer.start();
            consumer.start();
        }
    }


    /**
     * 利用await/signal方法实现思路
     */
    /**
     * 缓冲区容器类
     */
    static class Container1 {

        private Lock lock = new ReentrantLock();

        private Condition condition = lock.newCondition();

        private int capacity = 3;

        private LinkedList<Integer> list = new LinkedList<Integer>();


        /**
         * 添加数据到缓冲区
         * @param value
         */
        public void add(Integer value) {
            boolean flag = false;
            try {
                flag = lock.tryLock(3, TimeUnit.SECONDS);
                if(list.size() >= capacity){
                    System.out.println("生产者："+ Thread.currentThread().getName()+"，缓冲区已满,生产者进入waiting...");
                    // 进入等待状态
                    condition.await();
                }
                System.out.println("生产者："+ Thread.currentThread().getName()+"，add：" + value);
                list.add(value);

                //唤醒其他所有处于wait()的线程，包括消费者和生产者
                condition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(flag){
                    lock.unlock();
                }
            }
        }


        /**
         * 从缓冲区获取数据
         */
        public void get() {
            boolean flag = false;
            try {
                flag = lock.tryLock(3, TimeUnit.SECONDS);
                if(list.size() == 0){
                    System.out.println("消费者："+ Thread.currentThread().getName()+"，缓冲区为空,消费者进入waiting...");
                    // 进入等待状态
                    condition.await();
                }
                // 从头部获取数据，并移除元素
                Integer val = list.removeFirst();
                System.out.println("消费者："+ Thread.currentThread().getName()+"，value：" + val);

                //唤醒其他所有处于wait()的线程，包括消费者和生产者
                condition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(flag){
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 多生产者和消费者的实现思路
     * 上面介绍的都是一个生产者线程和一个消费者线程，模型比较简单。实际上，在业务开发中，经常会出现多个生产者线程和多个消费者线程，按照以上的实现思路，会出现什么情况呢？
     * 有可能会出现程序假死现象！下面我们来分析一下案例，假如有两个生产者线程a1、a2，两个消费者线程b1、b2，执行过程如下：
     * 1.生产者线程a1执行生产数据的操作，发现缓冲区数据已经填满了，然后进入等待阶段，同时向外发起通知，唤醒其它线程
     * 2.因为线程唤醒具有随机性，本应该唤醒消费者线程b1，结果可能生产者线程a2被唤醒，检查缓冲区数据已经填满了，又进入等待阶段，紧接向外发起通知，消费者线程得不到被执行的机会
     * 3.消费者线程b1、b2，也有可能会出现这个现象，本应该唤醒生产者线程，结果唤醒了消费者线程
     * 遇到这种情况，应该如何解决呢？
     * 因为ReentrantLock和Condition的结合，编程具有高度灵活性，我们可以采用这种组合解决多生产者和多消费者中的假死问题。
     */
    /**
     * 缓冲区容器类
     */
    static class ContainerDemo {

        private Lock lock = new ReentrantLock();
        private Condition producerCondition = lock.newCondition();
        private Condition consumerCondition = lock.newCondition();

        private int capacity = 3;
        private LinkedList<Integer> list = new LinkedList<Integer>();


        /**
         * 添加数据到缓冲区
         * @param value
         */
        public void add(Integer value) {
            boolean flag = false;
            try {
                flag = lock.tryLock(3, TimeUnit.SECONDS);
                if(list.size() >= capacity){
                    System.out.println("生产者："+ Thread.currentThread().getName()+"，缓冲区已满,生产者进入waiting...");
                    // 生产者进入等待状态
                    producerCondition.await();
                }
                System.out.println("生产者："+ Thread.currentThread().getName()+"，add：" + value);
                list.add(value);

                // 唤醒所有消费者处于wait()的线程
                consumerCondition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(flag){
                    lock.unlock();
                }
            }
        }


        /**
         * 从缓冲区获取数据
         */
        public void get() {
            boolean flag = false;
            try {
                flag = lock.tryLock(3, TimeUnit.SECONDS);
                if(list.size() == 0){
                    System.out.println("消费者："+ Thread.currentThread().getName()+"，缓冲区为空,消费者进入waiting...");
                    // 消费者进入等待状态
                    consumerCondition.await();
                }
                // 从头部获取数据，并移除元素
                Integer val = list.removeFirst();
                System.out.println("消费者："+ Thread.currentThread().getName()+"，value：" + val);

                // 唤醒所有生产者处于wait()的线程
                producerCondition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(flag){
                    lock.unlock();
                }
            }
        }
    }
    /**
     * 生产者
     */
    static class Producer1 extends Thread{

        private ContainerDemo container;

        private Integer value;

        public Producer1(ContainerDemo container, Integer value) {
            this.container = container;
            this.value = value;
        }

        @Override
        public void run() {
            container.add(value);
        }
    }
    /**
     * 消费者
     */
    static class Consumer1 extends Thread{

        private ContainerDemo container;

        public Consumer1(ContainerDemo container) {
            this.container = container;
        }

        @Override
        public void run() {
            container.get();
        }
    }

    /**
     * 测试类
     */
    static class MyThreadTest1 {

        public static void main(String[] args) {
            ContainerDemo container = new ContainerDemo();

            List<Thread> threadList = new ArrayList<>();
            // 初始化6个生产者线程
            for (int i = 0; i < 6; i++) {
                threadList.add(new Producer1(container, i));
            }
            // 初始化6个消费者线程
            for (int i = 0; i < 6; i++) {
                threadList.add(new Consumer1(container));
            }

            // 启动线程
            for (Thread thread : threadList) {
                thread.start();
            }
        }
    }


}
