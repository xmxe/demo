package com.xmxe.study_demo.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 根据计数器限流 AtomicInteger Semaphore
 */
public class LimitingByCounter {

    /**
     * 使用AomicInteger来进行统计当前正在并发执行的次数，如果超过域值就简单粗暴的直接响应给用户，说明系统繁忙，请稍后再试或其它跟业务相关的信息。
     * 弊端：使用 AomicInteger 简单粗暴超过域值就拒绝请求，可能只是瞬时的请求量高，也会拒绝请求。
     */
    private static AtomicInteger count = new AtomicInteger(0);//构造方法参数为初始值
 
    public static void execByAtomicInteger() {
        if (count.get() >= 5) {
            System.out.println("请求用户过多，请稍后在试！" + System.currentTimeMillis() / 1000);
        } else {
            count.incrementAndGet();
            try {
                //处理核心逻辑
                TimeUnit.SECONDS.sleep(1);
                System.out.println("--"+System.currentTimeMillis()/1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                count.decrementAndGet();
            }
        }
    }

    /**
     * 使用Semaphore信号量来控制并发执行的次数，如果超过域值信号量，则进入阻塞队列中排队等待获取信号量进行执行。
     * 如果阻塞队列中排队的请求过多超出系统处理能力，则可以在拒绝请求。
     * 相对Atomic优点：如果是瞬时的高并发，可以使请求在阻塞队列中排队，而不是马上拒绝请求，从而达到一个流量削峰的目的。
     */
    /**
     * acquire()  获取一个令牌，在获取到令牌、或者被其他线程调用中断之前线程一直处于阻塞状态。
​     * acquire(int permits)  获取一个令牌，在获取到令牌、或者被其他线程调用中断、或超时之前线程一直处于阻塞状态。
     * acquireUninterruptibly() 获取一个令牌，在获取到令牌之前线程一直处于阻塞状态（忽略中断）。
     * tryAcquire() 尝试获得令牌，返回获取令牌成功或失败，不阻塞线程。
​     * tryAcquire(long timeout, TimeUnit unit)尝试获得令牌，在超时时间内循环尝试获取，直到尝试获取成功或超时返回，不阻塞线程。
​     * release()释放一个令牌，唤醒一个获取令牌不成功的阻塞线程。
​     * hasQueuedThreads()等待队列里是否还存在等待线程。
     * getQueueLength()获取等待队列里阻塞的线程数。
​     * drainPermits() 清空令牌把可用令牌数置为0，返回清空令牌的数量。
​     * availablePermits()返回可用的令牌数量。
     */
    private static Semaphore semphore = new Semaphore(5);
 
    public static void execBySemaphore() {
        if(semphore.getQueueLength()>100){
            System.out.println("当前等待排队的任务数大于100，请稍候再试...");
        }
        try {
            semphore.acquire();
            // 处理核心逻辑
            TimeUnit.SECONDS.sleep(1);
            System.out.println("--" + System.currentTimeMillis() / 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semphore.release();
        }
    }
}

/**
 * 令牌桶算法 (Guava实现)
 * 令牌桶算法的原理是系统会以一个恒定的速度往桶里放入令牌，而如果请求需要被处理，则需要先从桶里获取一个令牌
 * 当桶里没有令牌可取时，则拒绝服务。 当桶满时，新添加的令牌被丢弃或拒绝。
 */
class TokenBucket{
    private static RateLimiter limiter = RateLimiter.create(5);
 
    public static void exec() {
        // limiter.acquire() 表示消费一个令牌 当桶中有足够的令牌时，则直接返回0，否则阻塞，直到有可用的令牌数才返回，返回的值为阻塞的时间。
        limiter.acquire(1);
        try {
            // 处理核心逻辑
            TimeUnit.SECONDS.sleep(1);
            System.out.println("--" + System.currentTimeMillis() / 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
