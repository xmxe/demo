package com.xmxe.study_demo.algorithm.limit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 使用Semaphore信号量来控制并发执行的次数，如果超过域值信号量，则进入阻塞队列中排队等待获取信号量进行执行。
 * 如果阻塞队列中排队的请求过多超出系统处理能力，则可以在拒绝请求。
 * 相对Atomic优点：如果是瞬时的高并发，可以使请求在阻塞队列中排队，而不是马上拒绝请求，从而达到一个流量削峰的目的。
 *
 * acquire()  获取一个令牌，在获取到令牌、或者被其他线程调用中断之前线程一直处于阻塞状态。
​ * acquire(int permits)  获取一个令牌，在获取到令牌、或者被其他线程调用中断、或超时之前线程一直处于阻塞状态。
 * acquireUninterruptibly() 获取一个令牌，在获取到令牌之前线程一直处于阻塞状态（忽略中断）。
 * tryAcquire() 尝试获得令牌，返回获取令牌成功或失败，不阻塞线程。
​ * tryAcquire(long timeout, TimeUnit unit)尝试获得令牌，在超时时间内循环尝试获取，直到尝试获取成功或超时返回，不阻塞线程。
​ * release() 释放一个令牌，唤醒一个获取令牌不成功的阻塞线程。
​ * hasQueuedThreads() 等待队列里是否还存在等待线程。
 * getQueueLength() 获取等待队列里阻塞的线程数。
​ * drainPermits() 清空令牌把可用令牌数置为0，返回清空令牌的数量。
​ * availablePermits() 返回可用的令牌数量。
 */
public class LimitingBySemaphore {
    
    public static void main(String[] args) {
        // demo1();
        demo2();
    }

    public static void demo1(){
        ExecutorService service = Executors.newFixedThreadPool(20);
        Semaphore semphore = new Semaphore(5);
        for(int i = 0;i < 20;i++){
            service.submit(()->{
                if(semphore.getQueueLength() > 10){
                    System.out.println("当前等待排队的任务数大于10，请稍候再试...");
                }
                try {
                    semphore.acquire();
                    // 处理核心逻辑
                    // TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread().getName()+"开始执行任务");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semphore.release();
                }
            });
        }
       service.shutdown();
    }

    /**
     * 模拟20个线程, 但是信号量只设置了5个许可
     * 因此线程是按序每2秒5个的打印job done.
     */
    public static void demo2(){
        class SemapDemo implements Runnable{
            // 设置5个许可
            final Semaphore semp = new Semaphore(5);
        
            @Override
            public void run() {
                try {
                    semp.acquire();
                    // 模拟线程耗时操作
                    Thread.sleep(2000L);
                    System.out.println("Job done! " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semp.release();
                }
            }
        }
        ExecutorService service = Executors.newFixedThreadPool(20);
        final SemapDemo demo = new SemapDemo();
        for (int i = 0; i < 20; i++) {
            service.submit(demo);
        }
        service.shutdown();
    }
}
