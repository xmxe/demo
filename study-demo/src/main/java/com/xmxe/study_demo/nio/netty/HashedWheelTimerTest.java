package com.xmxe.study_demo.nio.netty;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
 
import java.util.concurrent.TimeUnit;

/**
 * 时间轮算法实现超时订单删除
 * 时间轮算法可以类比于时钟，如上图箭头（指针）按某一个方向按固定频率轮动，每一次跳动称为一个tick。
 * 这样可以看出定时轮由个3个重要的属性参数，ticksPerWheel（一轮的tick数），tickDuration（一个tick的持续时间）以及 
 * timeUnit（时间单位），例如当ticksPerWheel=60，tickDuration=1，timeUnit=秒，这就和现实中的始终的秒针走动完全类似了。
 * 
 */
public class HashedWheelTimerTest {
    static class MyTimerTask implements TimerTask{
        boolean flag;
        public MyTimerTask(boolean flag){
            this.flag = flag;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
             System.out.println("要去数据库删除订单了。。。。");
             this.flag =false;
        }
    }
    public static void main(String[] args) {
        MyTimerTask timerTask = new MyTimerTask(true);
        Timer timer = new HashedWheelTimer();

        timer.newTimeout(timerTask, 15, TimeUnit.SECONDS);//延时15s后执行
        int i = 1;
        while(timerTask.flag){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i+"秒过去了");
            i++;
        }
    }
}
