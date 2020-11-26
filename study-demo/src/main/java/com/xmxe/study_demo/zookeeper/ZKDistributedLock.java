package com.xmxe.study_demo.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKDistributedLock implements Watcher {

    private int threadId;
    // 形如[第i个线程]
    private String THREAD_FLAG;
    // 连接zookeeper服务器用
    private ZooKeeper zk = null;

    // 分布式锁相关父节点路径
    private static final String GROUP_PATH = "/shared";
    // 分布式锁相关的子节点路径
    private static final String SUB_PATH = "/shared/node";

    // 当前临时顺序子节点
    private String currentEphemeralNode;
    // 比当前临时顺序子节点序号小的前面那个子节点
    private String preEphemeralNode;

    // 打印日志相关
    private static final Logger logger = Logger.getLogger(ZKDistributedLock.class);

    // 使用闭锁，必须计数为0，await方法才解除阻塞继续向下执行
    private CountDownLatch connectCountDownLatch = new CountDownLatch(1);

    // 使用闭锁，应对10个线程都抢到锁并处理完逻辑后，打印结果
    public static final CountDownLatch threadCountDownLatch = new CountDownLatch(5);

    // 记录process方法使用次数
    private int countProcess = 0;

    /**
     * 构造方法
     */
    public ZKDistributedLock(int threadId) {
        this.threadId = threadId;
        THREAD_FLAG = "[第" + threadId + "个线程]";
    }

    /**
     * 回调函数，返回连接结果
     *
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        // 一种跟连接相关
        KeeperState connectState = event.getState();
        // 一种跟事件类型相关，如获取锁的线程删除临时节点，释放锁
        EventType eventType = event.getType();
        // 必须同时满足两个条件，闭锁才能减1
        if (connectState == Event.KeeperState.SyncConnected && eventType == Event.EventType.None) {
            // 连接zookeeper成功
            logger.info(THREAD_FLAG + "连接zookeeper服务器成功");
            // 闭锁减一
            connectCountDownLatch.countDown();
        } else if (event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(preEphemeralNode)) {
            logger.info(THREAD_FLAG + "当前节点" + currentEphemeralNode + "前面节点" + preEphemeralNode + "被删除");
            try {
                if (isMinPath()) {
                    getDistributedLock();
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (Event.KeeperState.Disconnected == connectState) {
            logger.info(THREAD_FLAG + "与zookeeper服务器断开连接");
        } else if (Event.KeeperState.AuthFailed == connectState) {
            logger.info(THREAD_FLAG + "权限检查失败");
        } else if (Event.KeeperState.Expired == connectState) {
            logger.info(THREAD_FLAG + "与zookeeper服务器会话失效");
        }

        System.out.println(THREAD_FLAG + "执行" + (++countProcess) + "次process方法");

    }

    /**
     * 创建跟zookeeper服务器的连接
     *
     * @param connectAddress zookeeper服务器地址
     * @param sessionTimeout session超时时间
     */
    public void createConnection(String connectAddress, int sessionTimeout) throws IOException, InterruptedException {
        zk = new ZooKeeper(connectAddress, sessionTimeout, this);
        System.out.println(THREAD_FLAG + "开始连接zookeeper...");
        connectCountDownLatch.await();
        // 回调函数闭锁归零，才打印
        // System.out.println("连接zookeeper成功！");
    }

    /**
     * 创建组路径
     */
    public void createGroupPath() throws KeeperException, InterruptedException {
        // zookeeper中不存在就创建
        if (zk.exists(GROUP_PATH, true) == null) {
            String createdPath = zk.create(GROUP_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.warn(THREAD_FLAG + "创建" + createdPath + "成功");
        }

    }

    /**
     * 尝试获取锁
     */
    public void grabLock() throws KeeperException, InterruptedException {
        // 先创建临时节点
        currentEphemeralNode = zk.create(SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.info(THREAD_FLAG + "创建" + currentEphemeralNode + "临时顺序节点");
        // 检查是否可以获取锁
        if (isMinPath()) {
            // 如果是最小序号的节点，获取锁
            getDistributedLock();
        }
    }

    /**
     * 判断当前临时节点是否是最小序号的，如果不是最小节点，获取前面节点，并且设置监听
     * 
     * @return
     */
    private boolean isMinPath() throws KeeperException, InterruptedException {
        // 获取所有节点，并且排序
        List<String> nodes = zk.getChildren(GROUP_PATH, false);
        Collections.sort(nodes);
        // 判断当前创建的临时顺序节点是否是序号为0的，是0就是最小，可以获取锁
        String node = currentEphemeralNode.substring(GROUP_PATH.length() + 1);// 形如node0000?
        int index = nodes.indexOf(node);
        switch (index) {
            case -1:
                logger.info(THREAD_FLAG + "本节点不存在了" + node);
                return false;
            case 0:
                logger.info(THREAD_FLAG + node + "就是最小的子节点");
                return true;
            default:
                try {
                    // 找到比自己序号小1的临时节点
                    preEphemeralNode = GROUP_PATH + "/" + nodes.get(index - 1);
                    logger.info(THREAD_FLAG + currentEphemeralNode + "前面的节点是" + preEphemeralNode);
                    // 通过查询节点数据来设置监听
                    zk.getData(preEphemeralNode, true, new Stat());
                    return false;
                } catch (KeeperException e) {
                    if (zk.exists(preEphemeralNode, false) == null) {
                        logger.info(THREAD_FLAG + "排在" + currentEphemeralNode + "前面的子节点" + preEphemeralNode + "不存在");
                        // 出现异常，再次调用
                        return isMinPath();
                    } else {
                        throw e;
                    }
                }
        }
    }

    /**
     * 获取锁成功
     */
    private void getDistributedLock() throws KeeperException, InterruptedException {
        if (zk.exists(currentEphemeralNode, false) == null) {
            logger.info(THREAD_FLAG + "节点不存在");
            return;
        }
        logger.info(THREAD_FLAG + "获取锁成功");
        Thread.sleep(2000);
        System.out.println(THREAD_FLAG + "干完活了~~~");
        // 删除临时节点，不考虑版本
        zk.delete(currentEphemeralNode, -1);
        logger.info(THREAD_FLAG + "节点" + currentEphemeralNode + "已删除");
        // 释放与zookeeper服务器的连接
        if (zk != null) {
            zk.close();
        }
        logger.info(THREAD_FLAG + "释放了与zookeeper的连接");

        // 闭锁减1
        threadCountDownLatch.countDown();

    }

}
