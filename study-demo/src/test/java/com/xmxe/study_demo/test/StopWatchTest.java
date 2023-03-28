package com.xmxe.study_demo.test;

public class StopWatchTest {
    public static void main(String[] args) throws Exception {
        // 创建StopWatch的三种方式
        org.apache.commons.lang3.time.StopWatch stopWatch1 = new org.apache.commons.lang3.time.StopWatch();
        org.apache.commons.lang3.time.StopWatch stopWatch2 = org.apache.commons.lang3.time.StopWatch.create();
        // 这个方法不但会创建一个实例,同时还会启动计时
        org.apache.commons.lang3.time.StopWatch stopWatch3 = org.apache.commons.lang3.time.StopWatch.createStarted();

        // 休眠1秒
        Thread.sleep(1000);
        // 1002ms
        System.out.printf("耗时:%dms.\n",stopWatch3.getTime());

        // 暂停计时
        stopWatch3.suspend();
        Thread.sleep(1000);
        // 1000ms
        System.out.printf("暂停耗时:%dms.\n",stopWatch3.getTime());

        // 恢复计时
        stopWatch3.resume();
        Thread.sleep(1000);
        // 2001ms
        System.out.printf("恢复耗时:%dms.\n",stopWatch3.getTime());

        Thread.sleep(1000);
        // 停止计时
        stopWatch3.stop();
        Thread.sleep(1000);
        // 3009ms
        System.out.printf("总耗时:%dms.\n",stopWatch3.getTime());

        // 重置计时
        stopWatch3.reset();
        // 开始计时
        stopWatch3.start();
        Thread.sleep(1000);
        // 1000ms
        System.out.printf("重置耗时:%dms.\n",stopWatch3.getTime());

        org.springframework.util.StopWatch sw = new org.springframework.util.StopWatch();
        // 开始计时
        sw.start("任务1");
        // 休眠1秒
        Thread.sleep(1000);
        // 停止计时
        sw.stop();
        // 1002ms
        System.out.printf("任务1耗时:%d%s.\n", sw.getLastTaskTimeMillis(), "ms");

        Thread.sleep(1000);

        // 继续再新增2个任务:
        sw.start("任务2");
        Thread.sleep(1100);
        sw.stop();
        // 1100ms.
        System.out.printf("任务2耗时:%d%s.\n",sw.getLastTaskTimeMillis(),"ms");

        sw.start("任务3");
        Thread.sleep(1200);
        sw.stop();
        // 1203ms.
        System.out.printf("任务3耗时:%d%s.\n",sw.getLastTaskTimeMillis(),"ms");

        // 3.309373456s.
        System.out.printf("任务数量:%s,总耗时:%ss.\n",sw.getTaskCount(),sw.getTotalTimeSeconds());
        System.out.println(sw.prettyPrint());

    }

    /*
     * 总结一下这两种计时工具类优缺点:
     * commons-lang3中的StopWatch的用法比Spring中的要更简单一些
     * commons-lang3中的StopWatch功能比Spring中的要更灵活、更强大一些,支持暂停、恢复、重置等功能
     * Spring提供每个子任务名称,以及按格式化打印结果功能,针对多任务统计时更好一点
     */
}
