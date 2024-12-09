package com.xmxe.jdkfeature.thread.dynamic_threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        DynamicThreadPool dynamicThreadPool = new DynamicThreadPool(2, 4, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
        DynamicThreadPoolMBean mBean = new DynamicThreadPoolMBean(dynamicThreadPool);
        // 通过jconsole修改线程池信息的时候，我们就能看到修改的效果了
        // 在MBeans这个选项卡位置，我们可以看到刚刚配置的MBean，右侧的value则可以直接修改，修改之后，回到应用程序控制台，我们会发现线程相关数据已经发生变化了。
        while (true) {
            System.out.println("CorePoolSize:" + dynamicThreadPool.getThreadPoolExecutor().getCorePoolSize());
            System.out.println("MaximumPoolSize:" + dynamicThreadPool.getThreadPoolExecutor().getMaximumPoolSize());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}