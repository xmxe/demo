package com.xmxe.zookeeper;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * 分布式锁测试
 */
public class ZKDistributedLockTest {

    // private int threadId;

    // 连接zookeeper的地址
    private static final String CONNECT_ADDRESS = "192.168.236.133:2182";
    private static final int SESSION_TIMEOUT = 30000;

    // 打印日志相关
    private static final Logger logger = Logger.getLogger(ZKDistributedLockTest.class);

    // 主方法
    public static void main(String[] args) {
        BasicConfigurator.configure();
        // 启动5个线程,模拟5个client连接zookeeper服务器,创建临时有序节点并抢占锁
        for (int i = 0; i < 5; i++) {
            // 线程数从1开始
            int threadId = i + 1;
            // 启动线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 创建跟zookeeper服务器的连接
                        // 每个线程一个ZKDistributedLock示例 所以不用考虑currentEphemeralNode等属性的并发修改问题
                        ZKDistributedLock lock = new ZKDistributedLock(threadId);
                        lock.createConnection(CONNECT_ADDRESS, SESSION_TIMEOUT);
                        System.out.println("[第" + threadId + "个线程]连接成功,准备开始创建临时有序节点和抢占锁");
                        // 首先确认是否创建父节点/lock,只能有一个线程创建成功,zookeeper只会让一个线程创建成功
                        // 哪个线程先执行到这里,哪个线程就创建父节点
                        synchronized (ZKDistributedLock.threadCountDownLatch) {
                            lock.createGroupPath();
                        }
                        // 尝试获取分布式锁
                        lock.grabLock();
                    } catch (Exception e) {
                        logger.error("[第" + threadId + "个线程]抛出异常");
                        e.printStackTrace();
                    }
                }
            }, "第" + i + "个线程").start();
        }
        // 循环结束,提示所有线程执行完成
        try {
            ZKDistributedLock.threadCountDownLatch.await();
            logger.info("所有线程创建临时有序节点完成,并完成抢占锁后执行业务逻辑");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
