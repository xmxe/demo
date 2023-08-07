package com.xmxe.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡算法
 * 微信红包业务,为什么采用轮询算法？ https://mp.weixin.qq.com/s/HSgenPLZ22b3RDEZoewJaQ
 */
public class Balance {

    // 模拟配置的集群节点
    public static List<String> SERVERS = Arrays.asList(
            "44.120.110.001:8080",
            "44.120.110.002:8081",
            "44.120.110.003:8082",
            "44.120.110.004:8083",
            "44.120.110.005:8084");

    // 在之前是加入一个权重服务列表
    public static Map<String, Integer> WEIGHT_SERVERS = new LinkedHashMap<>();
    static {
        // 配置集群的所有节点信息及权重值
        WEIGHT_SERVERS.put("44.120.110.001:8080", 3);
        WEIGHT_SERVERS.put("44.120.110.002:8081", 2);
        WEIGHT_SERVERS.put("44.120.110.003:8082", 1);
    }

    // -------------------

    /**
     * 轮询策略类：实现基本的轮询算法
     */
    static class RoundRobin {
        // 用于记录当前请求的序列号
        private static AtomicInteger requestIndex = new AtomicInteger(0);

        // 从集群节点中选取一个节点处理请求
        public static String getServer() {
            // 用请求序列号取余集群节点数量,求得本次处理请求的节点下标
            int index = requestIndex.get() % SERVERS.size();
            // 从服务器列表中获取具体的节点IP地址信息
            String server = SERVERS.get(index);
            // 自增一次请求序列号,方便下个请求计算
            requestIndex.incrementAndGet();
            // 返回获取到的服务器IP地址
            return server;
        }
        // 测试类：测试轮询算法

        public static void main(String[] args) {
            // 使用for循环简单模拟10个客户端请求
            for (int i = 1; i <= 10; i++) {
                System.out.println("第" + i + "个请求：" + RoundRobin.getServer());
                // 第1个请求：44.120.110.001:8080
                // 第2个请求：44.120.110.002:8081
                // 第3个请求：44.120.110.003:8082
                // 第4个请求：44.120.110.004:8083
                // 第5个请求：44.120.110.005:8084
                // 第6个请求：44.120.110.001:8080
                // 第7个请求：44.120.110.002:8081
                // 第8个请求：44.120.110.003:8082
                // 第9个请求：44.120.110.004:8083
                // 第10个请求：44.120.110.005:8084
            }
        }

    }

    // ------

    /**
     * 随机策略类：随机抽取集群中的一个节点处理请求
     */
    static class Random {
        // 随机数产生器,用于产生随机因子
        static java.util.Random random = new java.util.Random();

        public static String getServer() {
            // 从已配置的服务器列表中,随机抽取一个节点处理请求
            return SERVERS.get(random.nextInt(SERVERS.size()));
        }
    }

    // ------

    /**
     * 随机权重算法
     */
    static class Randomweight {
        // 初始化随机数生产器
        static java.util.Random random = new java.util.Random();

        public static String getServer() {
            // 计算总权重值
            int weightTotal = 0;
            for (Integer weight : WEIGHT_SERVERS.values()) {
                weightTotal += weight;
            }

            // 从总权重的范围内随机生成一个索引
            int index = random.nextInt(weightTotal);
            System.out.println(index);

            // 遍历整个权重集群的节点列表,选择节点处理请求
            String targetServer = "";
            for (String server : WEIGHT_SERVERS.keySet()) {
                // 获取每个节点的权重值
                Integer weight = WEIGHT_SERVERS.get(server);
                // 如果权重值大于产生的随机数,则代表此次随机分配应该落入该节点
                if (weight > index) {
                    // 直接返回对应的节点去处理本次请求并终止循环
                    targetServer = server;
                    break;
                }
                // 如果当前节点的权重值小于随机索引,则用随机索引减去当前节点的权重值,
                // 继续循环权重列表,与其他的权重值进行对比,
                // 最终该请求总会落入到某个IP的权重值范围内
                index = index - weight;
            }
            // 返回选中的目标节点
            return targetServer;
        }

        public static void main(String[] args) {
            // 利用for循环模拟10个客户端请求测试
            for (int i = 1; i <= 10; i++) {
                System.out.println("第" + i + "个请求：" + getServer());
                // 第1个请求：44.120.110.003:8082
                // 第2个请求：44.120.110.001:8080
                // 第3个请求：44.120.110.003:8082
                // 第4个请求：44.120.110.003:8082
                // 第5个请求：44.120.110.003:8082
                // 第6个请求：44.120.110.003:8082
                // 第7个请求：44.120.110.003:8082
                // 第8个请求：44.120.110.001:8080
                // 第9个请求：44.120.110.001:8080
                // 第10个请求：44.120.110.002:8081
            }
        }
    }

    // ------

    /**
     * 轮询权重算法
     */
    static class RoundWeight {
        private static AtomicInteger requestCount = new AtomicInteger(0);

        public static String getServer() {
            int weightTotal = 0;
            for (Integer weight : WEIGHT_SERVERS.values()) {
                weightTotal += weight;
            }

            String targetServer = "";
            int index = requestCount.get() % weightTotal;
            requestCount.incrementAndGet();

            for (String server : WEIGHT_SERVERS.keySet()) {
                Integer weight = WEIGHT_SERVERS.get(server);
                if (weight > index) {
                    targetServer = server;
                    break;
                }
                index = index - weight;
            }
            return targetServer;
        }

        public static void main(String[] args) {
            for (int i = 1; i <= 10; i++) {
                System.out.println("第" + i + "个请求：" + getServer());

                // /********运行结果*********/
                // 第1个请求：44.120.110.001:8080
                // 第2个请求：44.120.110.001:8080
                // 第3个请求：44.120.110.001:8080
                // 第4个请求：44.120.110.001:8080
                // 第5个请求：44.120.110.001:8080
                // 第6个请求：44.120.110.001:8080
                // 第7个请求：44.120.110.001:8080
                // 第8个请求：44.120.110.001:8080
                // 第9个请求：44.120.110.001:8080
                // 第10个请求：44.120.110.001:8080
            }
        }
    }

    // ------

    /**
     * 权重类
     */
    static class Weight {
        // 节点信息
        private String server;
        // 节点权重值
        private Integer weight;
        // 动态权重值
        private Integer currentWeight;

        // 构造方法
        public Weight() {
        }

        public Weight(String server, Integer weight, Integer currentWeight) {
            this.server = server;
            this.weight = weight;
            this.currentWeight = currentWeight;
        }

        // 封装方法
        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public Integer getCurrentWeight() {
            return this.currentWeight;
        }

        public void setCurrentWeight(Integer currentWeight) {
            this.currentWeight = currentWeight;
        }
    }

    /**
     * 平滑加权轮询算法
     */
    static class RoundRobinWeight {
        // 初始化存储每个节点的权重容器
        private static Map<String, Weight> weightMap = new HashMap<>();

        // 计算总权重值,只需要计算一次,因此放在静态代码块中执行
        private static int weightTotal = 0;
        static {
            sumWeightTotal();
        }

        // 求和总权重值,后续动态伸缩节点时,再次调用该方法即可。
        public static void sumWeightTotal() {
            for (Integer weight : WEIGHT_SERVERS.values()) {
                weightTotal += weight;
            }
        }

        // 获取处理本次请求的具体服务器IP
        public static String getServer() {
            // 判断权重容器中是否有节点信息
            if (weightMap.isEmpty()) {
                // 如果没有则将配置的权重服务器列表挨个载入容器
                WEIGHT_SERVERS.forEach((servers, weight) -> {
                    // 初始化时,每个节点的动态权重值都为0
                    weightMap.put(servers, new Weight(servers, weight, 0));
                });
            }

            // 每次请求时,更改动态权重值
            for (Weight weight : weightMap.values()) {
                weight.setCurrentWeight(weight.getCurrentWeight()
                        + weight.getWeight());
            }

            // 判断权重容器中最大的动态权重值
            Weight maxCurrentWeight = null;
            for (Weight weight : weightMap.values()) {
                if (maxCurrentWeight == null || weight.getCurrentWeight() > maxCurrentWeight.getCurrentWeight()) {
                    maxCurrentWeight = weight;
                }
            }
            if (maxCurrentWeight != null) {
                // 最后用最大的动态权重值减去所有节点的总权重值
                maxCurrentWeight.setCurrentWeight(maxCurrentWeight.getCurrentWeight() - weightTotal);

                // 返回最大的动态权重值对应的节点IP
                return maxCurrentWeight.getServer();
            }
            throw new RuntimeException("maxCurrentWeight=null");
        }

        public static void main(String[] args) {
            // 使用for循环模拟6次请求
            for (int i = 1; i <= 6; i++) {
                System.out.println("第" + i + "个请求：" + getServer());
                // 第1个请求：44.120.110.001:8080
                // 第2个请求：44.120.110.002:8081
                // 第3个请求：44.120.110.001:8080
                // 第4个请求：44.120.110.003:8082
                // 第5个请求：44.120.110.002:8081
                // 第6个请求：44.120.110.001:8080
            }
        }
    }

    // ------

    /**
     * 一致性哈希算法
     */
    static class ConsistentHash {
        // 使用有序的红黑树结构,用于实现哈希环结构
        private static TreeMap<Integer, String> virtualNodes = new TreeMap<>();
        // 每个真实节点的虚拟节点数量
        private static final int VIRTUAL_NODES = 160;

        static {
            // 对每个真实节点添加虚拟节点,虚拟节点会根据哈希算法进行散列
            for (String serverIP : SERVERS) {
                // 将真实节点的IP映射到哈希环上
                virtualNodes.put(getHashCode(serverIP), serverIP);
                // 根据设定的虚拟节点数量进行虚拟节点映射
                for (int i = 0; i < VIRTUAL_NODES; i++) {
                    // 计算出一个虚拟节点的哈希值（只要不同即可）
                    int hash = getHashCode(serverIP + i);
                    // 将虚拟节点添加到哈希环结构上
                    virtualNodes.put(hash, serverIP);
                }
            }
        }

        public static String getServer(String IP) {
            int hashCode = getHashCode(IP);
            // 得到大于该Hash值的子红黑树
            SortedMap<Integer, String> sortedMap = virtualNodes.tailMap(hashCode);
            // 得到该树的第一个元素,也就是最小的元素
            Integer treeNodeKey = sortedMap.firstKey();

            // 如果没有大于该元素的子树了,则取整棵树的第一个元素,相当于取哈希环中的最小元素
            // if (sortedMap == null)
            //  treeNodeKey = virtualNodes.firstKey();

            // 返回对应的虚拟节点名称
            return virtualNodes.get(treeNodeKey);
        }

        // 哈希方法：用于计算一个IP的哈希值
        public static int getHashCode(String IP) {
            final int p = 1904390101;
            int hash = (int) 1901102097L;
            for (int i = 0; i < IP.length(); i++)
                hash = (hash ^ IP.charAt(i)) * p;
            hash += hash << 13;
            hash ^= hash >> 7;
            hash += hash << 3;
            hash ^= hash >> 17;
            hash += hash << 5;

            // 如果算出来的值为负数则取其绝对值
            if (hash < 0)
                hash = Math.abs(hash);
            return hash;
        }

        public static void main(String[] args) {
            // 用for循环模拟五个不同的IP访问
            for (int i = 1; i <= 5; i++) {
                System.out.println("第" + i + "个请求：" + getServer("192.168.12.13" + i));
            }
            System.out.println("-----------------------------");
            // 用for循环模拟三个相同的IP访问
            for (int i = 1; i <= 3; i++) {
                System.out.println("第" + i + "个请求：" + getServer("192.168.12.131"));
                /******** 输出结果 *******/
                // 第1个请求：44.120.110.002:8081
                // 第2个请求：44.120.110.003:8082
                // 第3个请求：44.120.110.004:8083
                // 第4个请求：44.120.110.003:8082
                // 第5个请求：44.120.110.004:8083
                // -----------------------------
                // 第1个请求：44.120.110.002:8081
                // 第2个请求：44.120.110.002:8081
                // 第3个请求：44.120.110.002:8081
            }
        }
    }

    // ------

    /**
     * 节点类：用于封装集群中的每个节点
     */
    static class Server {
        private String IP;
        private AtomicInteger active;
        // private Integer weight;

        public Server() {
        }

        public Server(String IP, int active) {
            this.IP = IP;
            // 将外部传递的活跃数作为默认活跃数
            this.active = new AtomicInteger(active);
        }

        public String getIP() {
            // 每分发一个请求时自增一次活跃数
            active.incrementAndGet();
            return IP;
        }

        public void setIP(String IP) {
            this.IP = IP;
        }

        public AtomicInteger getActive() {
            return active;
        }

        public String ping() {
            // 生成一个1000~3000之间的随机数
            int random = ThreadLocalRandom.current().nextInt(1000, 2000);
            try {
                // 随机休眠一段时间,模拟不同的响应速度
                Thread.sleep(random);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 最后返回自身的IP
            return this.IP;
        }
    }

    /**
     * 集群类：用于模拟集群节点列表
     */
    static class Servers {
        // 活跃度衰减器
        public static void attenuator() {
            new Thread(() -> {
                // 遍历集群中的所有节点
                for (Server server : Servers.SERVERS) {
                    // 如果活跃度不为0
                    if (server.getActive().get() != 0) {
                        // 则自减一个活跃度
                        server.getActive().getAndDecrement();
                    }
                }
                try {
                    // 每隔2秒中衰减一次活跃度
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 模拟的集群节点信息,活跃数最开始默认为0
        static List<Server> SERVERS = Arrays.asList(
                new Server("44.120.110.001:8080", 0),
                new Server("44.120.110.002:8081", 0),
                new Server("44.120.110.003:8082", 0));
    }

    /**
     * 最小活跃数算法实现类
     */
    static class LeastActive {

        public static String getServer() {
            // 初始化最小活跃数和最小活跃数的节点
            int leastActive = Integer.MAX_VALUE;
            Server leastServer = new Server();
            // 遍历集群中的所有节点
            for (Server server : Servers.SERVERS) {
                // 找出活跃数最小的节点
                if (leastActive > server.getActive().get()) {
                    leastActive = server.getActive().get();
                    leastServer = server;
                }
            }

            // 返回活跃数最小的节点IP
            return leastServer.getIP();
        }

        public static void main(String[] args) {
            Servers.attenuator();
            for (int i = 1; i <= 10; i++) {
                System.out.println("第" + i + "个请求：" + getServer());
                // 第1个请求：44.120.110.001:8080
                // 第2个请求：44.120.110.002:8081
                // 第3个请求：44.120.110.003:8082
                // 第4个请求：44.120.110.001:8080
                // 第5个请求：44.120.110.002:8081
                // 第6个请求：44.120.110.003:8082
                // 第7个请求：44.120.110.001:8080
                // 第8个请求：44.120.110.002:8081
                // 第9个请求：44.120.110.003:8082
                // 第10个请求：44.120.110.001:8080
            }
        }
    }

    // ------

    /**
     * 最优响应算法
     */
    static class ResponseTime {
        // 创建一个定长的线程池,用于去执行ping任务
        static ExecutorService pingServerPool = Executors.newFixedThreadPool(Servers.SERVERS.size());

        public static String getServer() throws InterruptedException {
            // 创建一个CompletableFuture用于拼接任务
            CompletableFuture<Object> cfAnyOf;
            // 创建一个接收结果返回的server节点对象
            final Server resultServer = new Server();
            // 根据集群节点数量初始化一个异步任务数组
            CompletableFuture<String>[] cfs = new CompletableFuture[Servers.SERVERS.size()];

            // 遍历整个服务器列表,为每个节点创建一个ping任务
            for (Server server : Servers.SERVERS) {
                // 获取当前节点在集群列表中的下标
                int index = Servers.SERVERS.indexOf(server);
                // 为每个节点创建一个ping任务,并交给pingServerPool线程池执行
                CompletableFuture<String> cf = CompletableFuture.supplyAsync(server::ping, pingServerPool);
                // 将创建好的异步任务加入数组中
                cfs[index] = cf;
            }

            // 将创建好的多个Ping任务组合成一个聚合任务并执行
            cfAnyOf = CompletableFuture.anyOf(cfs);

            // 监听执行完成后的回调,谁先执行完成则返回谁
            cfAnyOf.thenAccept(resultIP -> {
                System.out.println("最先响应检测请求的节点为：" + resultIP);
                resultServer.setIP((String) resultIP);
            });
            // 阻塞主线程一段时间,防止CompletableFuture退出
            Thread.sleep(3000);

            // 返回最先响应检测请求（ping）的节点作为本次处理客户端请求的节点
            return resultServer.getIP();
        }

        public static void main(String[] args) throws InterruptedException {
            for (int i = 1; i <= 5; i++) {
                System.out.println("第" + i + "个请求：" + getServer());
                /****** 运行结果： ******/
                // 最先响应检测请求的节点为：44.120.110.002:8081
                // 第1个请求：44.120.110.002:8081
                // 最先响应检测请求的节点为：44.120.110.002:8081
                // 第2个请求：44.120.110.002:8081
                // 最先响应检测请求的节点为：44.120.110.003:8082
                // 第3个请求：44.120.110.003:8082
                // 最先响应检测请求的节点为：44.120.110.003:8080
                // 第4个请求：44.120.110.001:8080
                // 最先响应检测请求的节点为：44.120.110.002:8081
                // 第5个请求：44.120.110.002:8081
            }
        }
    }

}
