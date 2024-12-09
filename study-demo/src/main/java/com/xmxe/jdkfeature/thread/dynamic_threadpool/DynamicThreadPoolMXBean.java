package com.xmxe.jdkfeature.thread.dynamic_threadpool;

public interface DynamicThreadPoolMXBean {
    int getCorePoolSize();
    void setCorePoolSize(int corePoolSize);
    int getMaximumPoolSize();
    void setMaximumPoolSize(int maximumPoolSize);
}
