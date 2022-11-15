package com.xmxe.study_demo.algorithm.limit;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 5种限流算法，7种限流方式，挡住突发流量？
 * https://mp.weixin.qq.com/s/xNvBdI99fKOsMFdoNC4K3w
 *
 * 常用的限流算法有哪些？
 * https://mp.weixin.qq.com/s/gsBl3J6iUEChODowLU9vjw
 * 
 * 新来个技术总监，把限流实现的那叫一个优雅，佩服！
 * https://mp.weixin.qq.com/s/lSrFOBZHSlneNUh_tnfxjg
 */
public class LimitAlgorithm {

    /**
     * 固定窗口算法
     * 固定窗口算法又叫计数器算法，是一种简单方便的限流算法。
     * 主要通过一个支持原子操作的计数器来累计1秒内的请求次数，当1秒内计数达到限流阈值时触发拒绝策略。
     * 每过1秒，计数器重置为0开始重新计数
     */
    static class RateLimiterSimpleWindow {
        // 阈值
        private static Integer QPS = 2;
        // 时间窗口（毫秒）
        private static long TIME_WINDOWS = 1000;
        // 计数器
        private static AtomicInteger REQ_COUNT = new AtomicInteger();

        private static long START_TIME = System.currentTimeMillis();

        public synchronized static boolean tryAcquire() {
            if ((System.currentTimeMillis() - START_TIME) > TIME_WINDOWS) {
                REQ_COUNT.set(0);
                START_TIME = System.currentTimeMillis();
            }
            return REQ_COUNT.incrementAndGet() <= QPS;
        }

        public static void main(String[] args) throws InterruptedException {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(250);
                LocalTime now = LocalTime.now();
                if (!tryAcquire()) {
                    System.out.println(now + " 被限流");
                } else {
                    System.out.println(now + " 做点什么");
                }
            }
        }
    }

    /**
     * 滑动窗口算法
     * 滑动窗口限流工具类
     */
    static class RateLimiterSlidingWindow {
        /**
         * 阈值
         */
        private int qps = 2;
        /**
         * 时间窗口总大小（毫秒）
         */
        private long windowSize = 1000;
        /**
         * 多少个子窗口
         */
        private Integer windowCount = 10;
        /**
         * 窗口列表
         */
        private WindowInfo[] windowArray = new WindowInfo[windowCount];

        public RateLimiterSlidingWindow(int qps) {
            this.qps = qps;
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < windowArray.length; i++) {
                windowArray[i] = new WindowInfo(currentTimeMillis, new AtomicInteger(0));
            }
        }

        /**
         * 1. 计算当前时间窗口
         * 2. 更新当前窗口计数 & 重置过期窗口计数
         * 3. 当前QPS是否超过限制
         *
         * @return
         */
        public synchronized boolean tryAcquire() {
            long currentTimeMillis = System.currentTimeMillis();
            // 1. 计算当前时间窗口
            int currentIndex = (int)(currentTimeMillis % windowSize / (windowSize / windowCount));
            // 2.  更新当前窗口计数 & 重置过期窗口计数
            int sum = 0;
            for (int i = 0; i < windowArray.length; i++) {
                WindowInfo windowInfo = windowArray[i];
                if ((currentTimeMillis - windowInfo.getTime()) > windowSize) {
                    windowInfo.getNumber().set(0);
                    windowInfo.setTime(currentTimeMillis);
                }
                if (currentIndex == i && windowInfo.getNumber().get() < qps) {
                    windowInfo.getNumber().incrementAndGet();
                }
                sum = sum + windowInfo.getNumber().get();
            }
            // 3. 当前QPS是否超过限制
            return sum <= qps;
        }

        private class WindowInfo {
            // 窗口开始时间
            private Long time;
            // 计数器
            private AtomicInteger number;

            public WindowInfo(long time, AtomicInteger number) {
                this.time = time;
                this.number = number;
            }
            public void setTime(Long time){
                this.time = time;
            }
            public Long getTime(){
                return time;
            }

            // public void setNumber(AtomicInteger number){
            //     this.number = number;
            // }
            public AtomicInteger getNumber(){
                return number;
            }
        }

        public static void main(String[] args) throws InterruptedException {
            int qps = 2, count = 20, sleep = 300, success = count * sleep / 1000 * qps;
            System.out.println(String.format("当前QPS限制为:%d,当前测试次数:%d,间隔:%dms,预计成功次数:%d", qps, count, sleep, success));
            success = 0;
            RateLimiterSlidingWindow myRateLimiter = new RateLimiterSlidingWindow(qps);
            for (int i = 0; i < count; i++) {
                Thread.sleep(sleep);
                if (myRateLimiter.tryAcquire()) {
                    success++;
                    if (success % qps == 0) {
                        System.out.println(LocalTime.now() + ": success, ");
                    } else {
                        System.out.print(LocalTime.now() + ": success, ");
                    }
                } else {
                    System.out.println(LocalTime.now() + ": fail");
                }
            }
            System.out.println();
            System.out.println("实际测试成功次数:" + success);
        }
    }


    /**
     * 滑动日志算法是实现限流的另一种方法，这种方法比较简单。基本逻辑就是记录下所有的请求时间点，
     * 新请求到来时先判断最近指定时间范围内的请求数量是否超过指定阈值，由此来确定是否达到限流，
     * 这种方式没有了时间窗口突变的问题，限流比较准确，但是因为要记录下每次请求的时间点，所以占用的内存较多。
     */
    /**
     * 滑动日志方式限流
     * 设置QPS为2.
     */
    static class RateLimiterSildingLog {

        /**
         * 阈值
         */
        private Integer qps = 2;
        /**
         * 记录请求的时间戳,和数量
         */
        private TreeMap<Long, Long> treeMap = new TreeMap<>();

        /**
         * 清理请求记录间隔, 60秒
         */
        private long claerTime = 60 * 1000;

        public RateLimiterSildingLog(Integer qps) {
            this.qps = qps;
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            // 清理过期的数据老数据，最长60秒清理一次
            if (!treeMap.isEmpty() && (treeMap.firstKey() - now) > claerTime) {
                Set<Long> keySet = new HashSet<>(treeMap.subMap(0L, now - 1000).keySet());
                for (Long key : keySet) {
                    treeMap.remove(key);
                }
            }
            // 计算当前请求次数
            int sum = 0;
            for (Long value : treeMap.subMap(now - 1000, now).values()) {
                sum += value;
            }
            // 超过QPS限制，直接返回false
            if (sum + 1 > qps) {
                return false;
            }
            // 记录本次请求
            if (treeMap.containsKey(now)) {
                treeMap.compute(now, (k, v) -> v + 1);
            } else {
                treeMap.put(now, 1L);
            }
            return sum <= qps;
        }

        public static void main(String[] args) throws InterruptedException {
            RateLimiterSildingLog rateLimiterSildingLog = new RateLimiterSildingLog(3);
            for (int i = 0; i < 10; i++) {
                Thread.sleep(250);
                LocalTime now = LocalTime.now();
                if (rateLimiterSildingLog.tryAcquire()) {
                    System.out.println(now + " 做点什么");
                } else {
                    System.out.println(now + " 被限流");
                }
            }
        }
    }

    /**
     * 漏桶算法中的漏桶是一个形象的比喻，这里可以用生产者消费者模式进行说明，请求是一个生产者，每一个请求都如一滴水，
     * 请求到来后放到一个队列（漏桶）中，而桶底有一个孔，不断的漏出水滴，就如消费者不断的在消费队列中的内容，
     * 消费的速率（漏出的速度）等于限流阈值。即假如QPS为2，则每1s/2=500ms 消费一次。漏桶的桶有大小，
     * 就如队列的容量，当请求堆积超过指定容量时，会触发拒绝策略。
     */


    /**
     * 令牌桶算法同样是实现限流是一种常见的思路，最为常用的Google的Java开发工具包Guava中的限流工具类RateLimiter
     * 就是令牌桶的一个实现。令牌桶的实现思路类似于生产者和消费之间的关系。系统服务作为生产者，按照指定频率向桶（容器）中添加
     * 令牌，如QPS为2，每500ms 向桶中添加一个令牌，如果桶中令牌数量达到阈值，则不再添加。
     * 请求执行作为消费者，每个请求都需要去桶中拿取一个令牌，取到令牌则继续执行；如果桶中无令牌可取，就触发拒绝策略，
     * 可以是超时等待，也可以是直接拒绝本次请求，由此达到限流目的。
     */

}
