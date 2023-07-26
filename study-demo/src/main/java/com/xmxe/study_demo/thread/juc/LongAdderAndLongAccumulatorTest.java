package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderAndLongAccumulatorTest {
    /**
     * LongAdder相比AtomicInteger拥有更高的性能.since jdk1.8
     * 阿里为什么推荐使用LongAdder https://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247558863&idx=1&sn=3c695968d16735e69d12680f4b588c48&scene=21#wechat_redirect
     */
    public static void longAdder() {
        LongAdder longAdder = new LongAdder();
        longAdder.increment();
    }

    /**
     * LongAccumulator提供了自定义的函数式接口，可以根据规则进行累加，不管是加减多少，或者是乘除多少，或者取最大值，想怎么弄，规则自己定
     * public LongAccumulator(LongBinaryOperator accumulatorFunction,long identity)
     * accumulatorFunction表示函数式接口参数 identity表示初始值
     */
    
    // 定义一个累加规则的LongAccumulator
    private static LongAccumulator longAccumulatorAdd = new LongAccumulator((left, right) -> left + right, 0);
    private static final int MAX_POOL_SIZE = 5, MAX_LOOP_SIZE = 10;

    public static void add(LongAccumulator longAccumulator, long number) throws InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            es.execute(() -> {
                for (int j = 0; j < MAX_LOOP_SIZE; j++) {
                    longAccumulator.accumulate(number);
                }
            });
        }
        es.shutdown();
        es.awaitTermination(5, TimeUnit.MINUTES);
        System.out.printf("LongAccumulator %s*%s +%s 结果：%s,耗时:%sms.\n",
                MAX_POOL_SIZE,
                MAX_LOOP_SIZE,
                number,
                longAccumulator.get(),
                (System.currentTimeMillis() - start));
        longAccumulator.reset();
    }

    // 定义一个取最大值规则的LongAccumulator
    private static LongAccumulator longAccumulatorMax = new LongAccumulator(Long::max, 50);

    private static void max(LongAccumulator longAccumulator) throws InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            int finalI = i;
            es.execute(() -> {
                longAccumulator.accumulate(finalI * 10);
            });
        }
        es.shutdown();
        es.awaitTermination(5, TimeUnit.MINUTES);
        System.out.printf("LongAccumulator 求最大值结果：%s,耗时:%sms.\n",
                longAccumulator.get(),
                (System.currentTimeMillis() - start));
        longAccumulator.reset();
    }

    public static void main(String[] args) throws Exception {
        // 和LongAdder一致
        add(longAccumulatorAdd, 1);
        // 每次累加2
        add(longAccumulatorAdd, 2);
        // 求最大值
        max(longAccumulatorMax);

        // 创建一个初始值为0的LongAccumulator
        LongAccumulator accumulator = new LongAccumulator((x, y) -> {
            System.out.println("x代表初始值-->"+x);
            System.out.println("y代表传入的值-->"+y);
            return x + y;
        }, 0);

        // 执行累加操作
        accumulator.accumulate(10);
        accumulator.accumulate(20);
        accumulator.accumulate(30);

        // 获取当前累加值
        long result = accumulator.get();
        System.out.println("累加结果：" + result);
    }

}
