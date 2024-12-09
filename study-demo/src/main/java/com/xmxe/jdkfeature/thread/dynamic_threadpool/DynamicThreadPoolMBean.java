package com.xmxe.jdkfeature.thread.dynamic_threadpool;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class DynamicThreadPoolMBean extends StandardMBean implements DynamicThreadPoolMXBean{
    
    private DynamicThreadPool dynamicThreadPool;

    public DynamicThreadPoolMBean(DynamicThreadPool dynamicThreadPool) throws Exception {
        super(DynamicThreadPoolMXBean.class);
        this.dynamicThreadPool = dynamicThreadPool;
        registerMBean();
    }

    private void registerMBean() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.javaboy:type=DynamicThreadPool");
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCorePoolSize() {
        return dynamicThreadPool.getThreadPoolExecutor().getCorePoolSize();
    }

    @Override
    public void setCorePoolSize(int corePoolSize) {
        dynamicThreadPool.setCorePoolSize(corePoolSize);
    }

    @Override
    public int getMaximumPoolSize() {
        return dynamicThreadPool.getThreadPoolExecutor().getMaximumPoolSize();
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        dynamicThreadPool.setMaximumPoolSize(maximumPoolSize);
    }
    
}
