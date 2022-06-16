package com.xmxe.study_demo.zookeeper.curator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class CuratorBase {
    // 会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;

    // 连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;

    // ZooKeeper服务地址
    private static final String CONNECT_ADDR = "192.168.1.1:2100,192.168.1.1:2101,192.168.1.1:2102";

    // 创建连接实例
    private CuratorFramework client = null;

    public static void main(String[] args) throws Exception {
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //2 通过工厂创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(CONNECT_ADDR).connectionTimeoutMs(CONNECTION_TIMEOUT)
                    .sessionTimeoutMs(SESSION_TIMEOUT)
                    .retryPolicy(retryPolicy)
//命名空间           .namespace("super")
                    .build();
        //3 开启连接
        client.start();

        System.out.println(States.CONNECTED);
        System.out.println(client.getState());

        //创建永久节点
        client.create().forPath("/curator","/curator data".getBytes());

        //创建永久有序节点
        client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/curator_sequential","/curator_sequential data".getBytes());

        //创建临时节点
        client.create().withMode(CreateMode.EPHEMERAL)
            .forPath("/curator/ephemeral","/curator/ephemeral data".getBytes());

        //创建临时有序节点
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator/ephemeral_path1","/curator/ephemeral_path1 data".getBytes());

        client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator/ephemeral_path2","/curator/ephemeral_path2 data".getBytes());

        //测试检查某个节点是否存在
        Stat stat1 = client.checkExists().forPath("/curator");
        Stat stat2 = client.checkExists().forPath("/curator2");

        System.out.println("'/curator'是否存在： " + (stat1 != null ? true : false));
        System.out.println("'/curator2'是否存在： " + (stat2 != null ? true : false));

        //获取某个节点的所有子节点
        System.out.println(client.getChildren().forPath("/"));

        //获取某个节点数据
        System.out.println(new String(client.getData().forPath("/curator")));

        //设置某个节点数据
        client.setData().forPath("/curator","/curator modified data".getBytes());

        //创建测试节点
        client.create().orSetData().creatingParentContainersIfNeeded()
            .forPath("/curator/del_key1","/curator/del_key1 data".getBytes());

        client.create().orSetData().creatingParentContainersIfNeeded()
        .forPath("/curator/del_key2","/curator/del_key2 data".getBytes());

        client.create().forPath("/curator/del_key2/test_key","test_key data".getBytes());

        //删除该节点
        client.delete().forPath("/curator/del_key1");

        //级联删除子节点
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/curator/del_key2");
    }

    /**
    * 事务管理：碰到异常，事务会回滚
    * @throws Exception
    */
    public void testTransaction() throws Exception{
        
       //定义几个基本操作
       CuratorOp createOp = client.transactionOp().create()
               .forPath("/curator/one_path","some data".getBytes());
       
       CuratorOp setDataOp = client.transactionOp().setData()
               .forPath("/curator","other data".getBytes());
       
       CuratorOp deleteOp = client.transactionOp().delete()
               .forPath("/curator");
       
       //事务执行结果
       List<CuratorTransactionResult> results = client.transaction()
               .forOperations(createOp,setDataOp,deleteOp);
       
       //遍历输出结果
       for(CuratorTransactionResult result : results){
           System.out.println("执行结果是： " + result.getForPath() + "--" + result.getType());
       }
       //因为节点“/curator”存在子节点，所以在删除的时候将会报错，事务回滚
    }

    /**
     * 监听器 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
     */
    public void listener() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        
        /**
         * 监听数据节点的变化情况
         */
        try(final NodeCache nodeCache = new NodeCache(client, "/zk-huey/cnode", false)){
            nodeCache.start(true);
            nodeCache.getListenable().addListener(
                new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        System.out.println("Node data is changed, new data: " + 
                            new String(nodeCache.getCurrentData().getData()));
                    }
                }, 
                pool
            );
        }catch(Exception e){
            e.printStackTrace();
        }
        
        /**
         * 监听子节点的变化情况
         */
        try(final PathChildrenCache childrenCache = new PathChildrenCache(client, "/zk-huey", true)){
            childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
            childrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                            throws Exception {
                            switch (event.getType()) {
                            case CHILD_ADDED:
                                System.out.println("CHILD_ADDED: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                                break;
                            case CHILD_UPDATED:
                                System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                                break;
                            default:
                                break;
                        }
                    }
                },
                pool
            );
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        // 测试
        // client.setData().forPath("/zk-huey/cnode", "world".getBytes());
        // Thread.sleep(10 * 1000);
        // pool.shutdown();
        // client.close();
        
    }
}
