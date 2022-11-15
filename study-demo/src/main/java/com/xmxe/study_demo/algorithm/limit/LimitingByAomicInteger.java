package com.xmxe.study_demo.algorithm.limit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 使用AomicInteger实现限流
 * 
 * 构造方法
 * public AtomicInteger(int initialValue)   创建具有给定初始值的新AtomicInteger。参数：initialValue - 初始值
 * public AtomicInteger()  创建具有初始值0的新AtomicInteger。
 * 
 * public final int get()  获取当前值。返回：当前值
 * public final void set(int newValue)  设置为给定值。参数：newValue - 新值
 * public final void lazySet(int newValue) 不使用内存屏障设置 最后设置为给定值。参数：newValue - 新值
 * 
 * 从以下版本开始：1.6 
 * public final int getAndSet(int newValue)   以原子方式设置为给定值，并返回旧值。参数：newValue - 新值 返回：以前的值
 * public final boolean compareAndSet(int expect, int update)  CAS实现 比较并交换 一种不依靠锁实现并发安全的技术 如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值。参数：expect - 预期值   update - 新值 返回：如果成功，则返回true。返回False指示实际值与预期值不相等。
 * public final boolean weakCompareAndSet(int expect,  int update) 与compareAndSet实现一样 只是针对不同平台的架构 如果当前值 == 预期值，则以原子方式将该设置为给定的更新值。可能意外失败并且不提供排序保证，所以只有在很少的情况下才对compareAndSet进行适当地选择。 参数：expect - 预期值   update - 新值 返回：如果成功，则返回true。
 * public final int getAndIncrement()   以原子方式将当前值加1。返回：以前的值
 * public final int getAndDecrement()    以原子方式将当前值减1。返回：以前的值
 * public final int getAndAdd(int delta)    以原子方式将给定值与当前值相加。参数：delta - 要加上的值 返回：以前的值
 * public final int incrementAndGet()    以原子方式将当前值加1。返回：更新的值
 * public final int decrementAndGet()   以原子方式将当前值减1。返回：更新的值
 * public final int addAndGet(int delta)   以原子方式将给定值与当前值相加。参数：delta - 要加上的值 返回：更新的值
 * public StringtoString()   返回当前值的字符串表示形式 覆盖：类Object中的toString 返回：当前值的字符串表示形式。
 * public int intValue()   从类Number复制的描述   以int形式返回指定的数值。这可能会涉及到舍入或取整。指定者：类Number中的intValue 返回：转换为int类型后该对象表示的数值。
 * public long longValue()   从类Number复制的描述 以long形式返回指定的数值。这可能涉及到舍入或取整。指定者：类Number中的longValue 返回：转换为long类型后该对象表示的数值。
 * public float floatValue() 从类Number复制的描述 以float形式返回指定的数值。这可能会涉及到舍入。指定者：类Number中的floatValue 返回：转换为float类型后该对象表示的数值。
 * public double doubleValue() 从类Number复制的描述 以double形式返回指定的数值。这可能会涉及到舍入。 指定者：类Number中的doubleValue 返回：转换为double类型后该对象表示的数值。
 **/
public class LimitingByAomicInteger {

    /**
     * 使用AomicInteger来进行统计当前正在并发执行的次数，如果超过域值就简单粗暴的直接响应给用户，
     * 说明系统繁忙,请稍后再试或其它跟业务相关的信息。
     * 弊端：使用AomicInteger简单粗暴超过域值就拒绝请求，可能只是瞬时的请求量高，也会拒绝请求。
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
     * LongAdder相比AtomicInteger拥有更高的性能 
     * since jdk1.8
     */
    public static void longAdder(){
        LongAdder longAdder = new LongAdder();
        longAdder.increment();
    }
}
