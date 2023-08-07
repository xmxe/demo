package com.xmxe.zookeeper.curator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

import org.springframework.util.CollectionUtils;
import com.google.common.base.Charsets;

/**
 * zookeeper工具类
 * 
 */
public class CuratorClientUtil {

    // 会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;

    // 连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;

    // ZooKeeper服务地址
    // private static final String CONNECT_ADDR = "192.168.1.1:2100,192.168.1.1:2101,192.168.1.1:2102";

    private volatile static CuratorFramework client; // 客户端

    public static CuratorFramework getInstance(String zkAddress) {
        if (client == null) {
            synchronized (CuratorFramework.class) {
                if (client == null) {
                    client = CuratorFrameworkFactory.builder().connectString(zkAddress)
                            .sessionTimeoutMs(SESSION_TIMEOUT).connectionTimeoutMs(CONNECTION_TIMEOUT)
                            .canBeReadOnly(true).namespace("namespace")
                            .retryPolicy(new ExponentialBackoffRetry(1000, 10)).defaultData(null).build();

                    // client.getCuratorListenable().addListener(new ZKNodeEventListener());

                    client.start();
                }
            }
        }
        return client;
    }

    /**
     * 创建节点
     * 
     * @param nodeName
     * @param value
     * @return
     */
    public static boolean createNode(CuratorFramework client, String nodeName, String value) {
        boolean isSuccessFlag = false; // 标志
        try {
            Stat stat = client.checkExists().forPath(nodeName); // 检查节点是否存在
            if (stat == null) {
                String opResult = null;
                if (StringUtils.isEmpty(value)) // 判断节点数据是否是空的
                    opResult = client.create().creatingParentsIfNeeded().forPath(nodeName);
                else
                    // 不为空就设置节点的数据值
                    opResult = client.create().creatingParentsIfNeeded().forPath(nodeName,
                            value.getBytes(Charsets.UTF_8));

                // isSuccessFlag = StringUtils.isSame(nodeName, opResult);
                if (nodeName.equals(opResult))
                    isSuccessFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccessFlag;
    }

    /**
     * 更新节点
     * 
     * @param path
     * @param value
     * @return
     */
    public static boolean updateNode(CuratorFramework client, String path, String value) {
        boolean isSuccessFlag = false;
        try {
            Stat stat = client.checkExists().forPath(path); // 校验一下是否这个节点是否存在
            if (stat != null) { // 存在就开始更新节点数据
                Stat returnResut = client.setData().forPath(path, value.getBytes(Charsets.UTF_8));

                if (returnResut != null) {
                    isSuccessFlag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return isSuccessFlag;
    }

    /**
     * 删除节点
     * 
     * @param path
     * 
     */
    public static void delNode(CuratorFramework client, String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取client
     * 
     * @return
     */
    public CuratorFramework getClient() {
        return client;
    }

    /**
     * 获取指定节点下的所有子节点的名称与值
     * 
     * @param path
     * @return
     */
    public static Map<String, String> showChildrenDetail(CuratorFramework client, String path) {
        Map<String, String> nodeMap = new HashMap<String, String>();
        try {
            GetChildrenBuilder childrenBuilder = client.getChildren();
            List<String> childrenList = childrenBuilder.forPath(path);
            GetDataBuilder dataBuilder = client.getData();
            if (!CollectionUtils.isEmpty(childrenList)) {
                childrenList.forEach(item -> {
                    String propPath = ZKPaths.makePath(path, item);
                    try {
                        nodeMap.put(item, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodeMap;
    }

    /**
     * 列出节点下所有的子节点,但是不带子节点的数据
     * 
     * @param path
     * @return
     */
    public static List<String> showChildren(CuratorFramework client, String path) {
        List<String> childenList = new ArrayList<String>();
        try {
            GetChildrenBuilder childrenBuilder = client.getChildren();
            childenList = childrenBuilder.forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childenList;
    }

    /**
     * 对节点则增加监听,flag如果是true,就对节点本身监听,false是该节点的子节点增加监听
     * 
     * @param path
     * @param flag
     * @throws Exception
     * 
     */
    public static void addWatch(CuratorFramework client, String path, boolean flag) throws Exception {
        if (flag)
            client.getData().watched().forPath(path);
        else
            client.getChildren().watched().forPath(path);
    }

    /**
     * 销毁资源
     */
    public static void destory() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 对节点则增加监听,flag如果是true,就对节点本身监听,false是该节点的子节点增加监听
     * 
     * @param path
     * @param flag
     * @param watcher 监视节点
     * @throws Exception
     * 
     */
    public static void addWatch(CuratorFramework client, String path, boolean flag, CuratorWatcher watcher)
            throws Exception {
        if (flag)
            client.getData().usingWatcher(watcher).forPath(path);
        else
            client.getChildren().usingWatcher(watcher).forPath(path);
    }

    // 自定义监听器
    final class ZKNodeEventListener implements CuratorListener {

        @Override
        public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
            System.out.println(event.toString() + "............");
            final WatchedEvent wathEvent = event.getWatchedEvent();
            if (wathEvent != null) {
                System.out.println(wathEvent.getState() + "----------" + wathEvent.getType());
                if (wathEvent.getState() == KeeperState.SyncConnected) {
                    switch (wathEvent.getType()) {
                        case NodeChildrenChanged:
                            // do
                            break;
                        case NodeDataChanged:
                            // do
                            break;
                        default:
                            break;

                    }
                }
            }
        }
    }

    // 监视
    final class ZKNodeWather implements CuratorWatcher {

        @Override
        public void process(WatchedEvent event) throws Exception {
            if (event.getType() == EventType.NodeChildrenChanged) {
                System.out.println("监视节点");
            }

        }
    }
}