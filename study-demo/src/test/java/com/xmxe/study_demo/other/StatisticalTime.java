package com.xmxe.study_demo.other;

/**
 * 别再用System.currentTimeMillis()统计耗时了,StopWatch好用到爆！ https://mp.weixin.qq.com/s/f-gJxN-wbLWK9WvIjv8vHw
 */
public class StatisticalTime {
    public static void main(String[] args) throws Exception{
        // 创建StopWatch的三种方式
        org.apache.commons.lang3.time.StopWatch stopWatch1 = new org.apache.commons.lang3.time.StopWatch();
        org.apache.commons.lang3.time.StopWatch stopWatch2 = org.apache.commons.lang3.time.StopWatch.create();
        // 这个方法不但会创建一个实例，同时还会启动计时
        org.apache.commons.lang3.time.StopWatch stopWatch3 = org.apache.commons.lang3.time.StopWatch.createStarted();
        
        // 休眠1秒
        Thread.sleep(1000);
        // 1002ms
        System.out.printf("耗时：%dms.\n", stopWatch3.getTime()); 

        // 暂停计时
        stopWatch3.suspend();
        Thread.sleep(1000);
        // 1000ms
        System.out.printf("暂停耗时：%dms.\n", stopWatch3.getTime());

        // 恢复计时
        stopWatch3.resume();
        Thread.sleep(1000);
        // 2001ms
        System.out.printf("恢复耗时：%dms.\n", stopWatch3.getTime());

        Thread.sleep(1000);
        // 停止计时
        stopWatch3.stop();
        Thread.sleep(1000);
        // 3009ms
        System.out.printf("总耗时：%dms.\n", stopWatch3.getTime()); 

        // 重置计时
        stopWatch3.reset();
        // 开始计时
        stopWatch3.start();
        Thread.sleep(1000);
        // 1000ms
        System.out.printf("重置耗时：%dms.\n", stopWatch3.getTime());


        org.springframework.util.StopWatch sw = new org.springframework.util.StopWatch();
        // 开始计时
        sw.start("任务1");
        // 休眠1秒
        Thread.sleep(1000);
        // 停止计时
        sw.stop();
        // 1002ms
        System.out.printf("任务1耗时：%d%s.\n", sw.getLastTaskTimeMillis(), "ms");
        System.out.println(sw.prettyPrint());

    }
}
