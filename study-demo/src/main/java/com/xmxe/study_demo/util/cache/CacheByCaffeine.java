package com.xmxe.study_demo.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 
 * 学习下真正的缓存之王，以及在Spring Boot中的使用！https://mp.weixin.qq.com/s/lmneEosfILnp6g30MrYPjg
 */
public class CacheByCaffeine {

    /**
     * 通过Map的底层方式，直接将需要缓存的对象放在内存中
     * 优点：简单粗暴，不需要引入第三方包，比较适合一些比较简单的场景。
     * 缺点：没有缓存淘汰策略，定制化开发成本高。
     */
    static class LRUCache<K,V> extends LinkedHashMap<K,V>{

        /**
         * 可重入读写锁，保证并发读写安全性
         */
        private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        /**
         * 缓存大小限制
         */
        private int maxSize;

        public LRUCache(int maxSize) {
            super(maxSize + 1, 1.0f, true);
            this.maxSize = maxSize;
        }

        @Override
        public V get(Object key) {
            readLock.lock();
            try {
                return super.get(key);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public V put(K key, V value) {
            writeLock.lock();
            try {
                return super.put(key, value);
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
            return this.size() > maxSize;
        }
    }


    /**
     * Caffeine采用了W-TinyLFU（LUR和LFU的优点结合）开源的缓存技术。缓存性能接近理论最优，属于是Guava Cache的增强版
     */
    static class CaffeineCacheTest {

        public static void main(String[] args) throws Exception {
            // 创建guava cache
            com.github.benmanes.caffeine.cache.Cache<String, String> loadingCache = Caffeine.newBuilder()
                    // cache的初始容量
                    .initialCapacity(5)
                    // cache最大缓存数
                    .maximumSize(10)
                    // 设置写缓存后n秒钟过期
                    .expireAfterWrite(17, TimeUnit.SECONDS)
                    // 设置读写缓存后n秒钟过期,实际很少用到,类似于expireAfterWrite
                    // .expireAfterAccess(17,TimeUnit.SECONDS)
                    .build();
            String key = "key";
            // 往缓存写数据
            loadingCache.put(key, "v");

            // 获取value的值，如果key不存在，获取value后再返回
            String value = loadingCache.get(key, CaffeineCacheTest::getValueFromDB);

            // 删除key
            loadingCache.invalidate(key);
        }

        private static String getValueFromDB(String key) {
            return "v";
        }
    }
}

    // static class CaffeineClass {

        // ------------ 缓存类型---------------
        
        /**
         * Cache接口提供了显式搜索查找、更新和移除缓存元素的能力。当缓存的元素无法生成或者在生成的过程中抛出异常而导致生成元素失败，cache.get也许会返回null。
         */
        // Cache<Key, Graph> cache = Caffeine.newBuilder()
        //         .expireAfterWrite(10,TimeUnit.MINUTES)
        //         .maximumSize(10_000)
        //         .build();
        // // 查找一个缓存元素，没有查找到的时候返回null
        // Graph graph = cache.getIfPresent(key);
        // // 查找缓存，如果缓存不存在则生成缓存元素,如果无法生成则返回null
        // graph=cache.get(key,k->createExpensiveGraph(key));
        // // 添加或者更新一个缓存元素
        // cache.put(key,graph);
        // // 移除一个缓存元素
        // cache.invalidate(key);


        /**
         * 一个LoadingCache是一个Cache附加上CacheLoader能力之后的缓存实现。
         * 如果缓存不错在，则会通过CacheLoader.load来生成对应的缓存元素。
         */
        // LoadingCache<Key, Graph> cache = Caffeine.newBuilder()
        //                      .maximumSize(10_000)
        //                      .expireAfterWrite(10,TimeUnit.MINUTES)
        //                      .build(key -> createExpensiveGraph(key));
        // // 查找缓存，如果缓存不存在则生成缓存元素,如果无法生成则返回null
        // Graph graph = cache.get(key);
        // // 批量查找缓存，如果缓存不存在则生成缓存元素
        // Map<Key, Graph> graphs = cache.getAll(keys);

        /**
         * AsyncCache就是Cache的异步形式，提供了Executor生成缓存元素并返回CompletableFuture的能力。默认的线程池实现是ForkJoinPool.commonPool()，当然你也可以通过覆盖并实现Caffeine.executor(Executor)方法来自定义你的线程池选择。
         */
        // AsyncCache<Key, Graph> cache = Caffeine.newBuilder()
        //                      .expireAfterWrite(10,TimeUnit.MINUTES)
        //                      .maximumSize(10_000)
        //                      .buildAsync();
        // // 查找一个缓存元素，没有查找到的时候返回null
        // CompletableFuture<Graph> graph = cache.getIfPresent(key);
        // // 查找缓存元素，如果不存在，则异步生成
        // graph = cache.get(key, k -> createExpensiveGraph(key));
        // // 添加或者更新一个缓存元素
        // cache.put(key,graph);
        // // 移除一个缓存元素
        // cache.synchronous().invalidate(key);


        /**
         * AsyncLoadingCache就是LoadingCache的异步形式，提供了异步load生成缓存元素的功能。
         */  
        // AsyncLoadingCache<Key, Graph> cache = Caffeine.newBuilder()
        //                          .maximumSize(10_000)
        //                          .expireAfterWrite(10,TimeUnit.MINUTES)
        //                          // 你可以选择:去异步的封装一段同步操作来生成缓存元素
        //                          .buildAsync(key -> createExpensiveGraph(key));
        //                          // 你也可以选择:构建一个异步缓存元素操作并返回一个future
        //                          .buildAsync((key,executor) -> createExpensiveGraphAsync(key,executor));
        // // 查找缓存元素，如果其不存在，将会异步进行生成
        // CompletableFuture<Graph> graph = cache.get(key);
        // // 批量查找缓存元素，如果其不存在，将会异步进行生成
        // CompletableFuture<Map<Key, Graph>> graphs = cache.getAll(keys);


        // -------------- 驱逐策略-------------

        /**
         * 基于容量
         */
        // 基于缓存内的元素个数进行驱逐
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .maximumSize(10_000)
        //                          .build(key -> createExpensiveGraph(key));

        // // 基于缓存内元素权重进行驱逐
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .maximumWeight(10_000)
        //                          .weigher((Key key, Graph graph) -> graph.vertices().size())
        //                          .build(key -> createExpensiveGraph(key));

        /**
         * 基于时间
         */
        // 基于固定的过期时间驱逐策略
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .expireAfterAccess(5,TimeUnit.MINUTES)
        //                          .build(key -> createExpensiveGraph(key));
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .expireAfterWrite(10,TimeUnit.MINUTES)
        //                          .build(key -> createExpensiveGraph(key));

        // // 基于不同的过期驱逐策略
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        // .expireAfter(new Expiry<Key, Graph>() {
        //     public long expireAfterCreate(Key key, Graph graph, long currentTime) {
        //     // Use wall clock time, rather than nanotime, if from an external resource
        //          long seconds = graph.creationDate().plusHours(5)
        //              .minus(System.currentTimeMillis(), MILLIS)
        //              .toEpochSecond();
        //          return TimeUnit.SECONDS.toNanos(seconds);
        //     }
        //     public long expireAfterUpdate(Key key, Graph graph, long currentTime, long currentDuration) {
        //          return currentDuration;
        //     }
        //     public long expireAfterRead(Key key, Graph graph,long currentTime, long currentDuration) {
        //          return currentDuration;
        //     }
        // })
        // .build(key -> createExpensiveGraph(key));


        /**
        * 基于引用
        */
        // 当key和缓存元素都不再存在其他强引用的时候驱逐
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .weakKeys()
        //                          .weakValues()
        //                          .build(key -> createExpensiveGraph(key));
        // // 当进行GC的时候进行驱逐
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .softValues()
        //                          .build(key -> createExpensiveGraph(key));

        // }

        // --------------刷新机制---------------

        /**
         * 只有在LoadingCache中可以使用刷新策略，与驱逐不同的是，在刷新的时候如果查询缓存元素，其旧值将仍被返回，直到该元素的刷新完毕后结束后才会返回刷新后的新值。
         */
        // LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
        //                          .maximumSize(10_000)
        //                          .refreshAfterWrite(1, TimeUnit.MINUTES)
        //                          .build(key -> createExpensiveGraph(key));

        // -----------统计--------------------------------
        /**
         * 通过使用Caffeine.recordStats()方法可以打开数据收集功能。Cache.stats()方法将会返回一个CacheStats对象，其将会含有一些统计指标，比如：
         * hitRate():查询缓存的命中率
         * evictionCount():被驱逐的缓存数量
         * averageLoadPenalty():新值被载入的平均耗时
         * 配合SpringBoot提供的RESTful Controller，能很方便的查询Cache的使用情况。
         */
        // Cache<Key, Graph> graphs = Caffeine.newBuilder()
        //         .maximumSize(10_000)
        //         .recordStats()
        //         .build();


        // ---------------Caffeine在SpringBoot的实战-----------

        // 按照Caffeine Github官网文档的描述，Caffeine是基于Java8的高性能缓存库。并且在Spring5（SpringBoot2.x)官方放弃了Guava，而使用了性能更优秀的Caffeine作为默认的缓存方案。
        // SpringBoot使用Caffeine有两种方式：
        // 方式一：直接引入Caffeine依赖，然后使用Caffeine的函数实现缓存 方式二：引入Caffeine和Spring Cache依赖，使用SpringCache注解方法实现缓存
        // 下面分别介绍两种使用方式。
        // 方式一：使用Caffeine依赖其次，设置缓存的配置选项
        // @Configuration
        // public class CacheConfig {

        //     @Bean
        //     public Cache<String, Object> caffeineCache() {
        //         return Caffeine.newBuilder()
        //                 // 设置最后一次写入或访问后经过固定时间过期
        //                 .expireAfterWrite(60, TimeUnit.SECONDS)
        //                 // 初始的缓存空间大小
        //                 .initialCapacity(100)
        //                 // 缓存的最大条数
        //                 .maximumSize(1000)
        //                 .build();
        //     }

        // }

        // 最后给服务添加缓存功能
        // @Service
        // public class UserInfoServiceImpl {

        //     /**
        //      * 模拟数据库存储数据
        //      */
        //     private HashMap<Integer, UserInfo> userInfoMap = new HashMap<>();

        //     @Autowired
        //     Cache<String, Object> caffeineCache;

        //     public void addUserInfo(UserInfo userInfo) {
        //         userInfoMap.put(userInfo.getId(), userInfo);
        //         // 加入缓存
        //         caffeineCache.put(String.valueOf(userInfo.getId()), userInfo);
        //     }

        //     public UserInfo getByName(Integer id) {
        //         // 先从缓存读取
        //         caffeineCache.getIfPresent(id);
        //         UserInfo userInfo = (UserInfo) caffeineCache.asMap().get(String.valueOf(id));
        //         if (userInfo != null) {
        //             return userInfo;
        //         }
        //         // 如果缓存中不存在，则从库中查找
        //         userInfo = userInfoMap.get(id);
        //         // 如果用户信息不为空，则加入缓存
        //         if (userInfo != null) {
        //             caffeineCache.put(String.valueOf(userInfo.getId()), userInfo);
        //         }
        //         return userInfo;
        //     }

        //     public UserInfo updateUserInfo(UserInfo userInfo) {
        //         if (!userInfoMap.containsKey(userInfo.getId())) {
        //             return null;
        //         }
        //         // 取旧的值
        //         UserInfo oldUserInfo = userInfoMap.get(userInfo.getId());
        //         // 替换内容
        //         if (!StringUtils.isEmpty(oldUserInfo.getAge())) {
        //             oldUserInfo.setAge(userInfo.getAge());
        //         }
        //         if (!StringUtils.isEmpty(oldUserInfo.getName())) {
        //             oldUserInfo.setName(userInfo.getName());
        //         }
        //         if (!StringUtils.isEmpty(oldUserInfo.getSex())) {
        //             oldUserInfo.setSex(userInfo.getSex());
        //         }
        //         // 将新的对象存储，更新旧对象信息
        //         userInfoMap.put(oldUserInfo.getId(), oldUserInfo);
        //         // 替换缓存中的值
        //         caffeineCache.put(String.valueOf(oldUserInfo.getId()), oldUserInfo);
        //         return oldUserInfo;
        //     }

        //     @Override
        //     public void deleteById(Integer id) {
        //         userInfoMap.remove(id);
        //         // 从缓存中删除
        //         caffeineCache.asMap().remove(String.valueOf(id));
        //     }

        // }

        // 方式二：使用Spring Cache注解

        // 首先引入maven相关依赖
        // <dependency>
        //      <groupId>org.springframework.boot</groupId>
        //      <artifactId>spring-boot-starter-cache</artifactId>
        // </dependency>
        // <dependency>
        //      <groupId>com.github.ben-manes.caffeine</groupId>
        //      <artifactId>caffeine</artifactId>
        // </dependency>
        // 其次，配置缓存管理类
        // @Configuration
        // public class CacheConfig {
        //     /**
        //      * 配置缓存管理器
        //      * 
        //      * @return 缓存管理器
        //      */
        //     @Bean("caffeineCacheManager")
        //     public CacheManager cacheManager() {
        //         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        //         cacheManager.setCaffeine(Caffeine.newBuilder()
        //                 // 设置最后一次写入或访问后经过固定时间过期
        //                 .expireAfterAccess(60,TimeUnit.SECONDS)
        //                 // 初始的缓存空间大小
        //                 .initialCapacity(100)
        //                 // 缓存的最大条数
        //                 .maximumSize(1000));
        //         return cacheManager;
        //     }

        // }

        // // 最后给服务添加缓存功能
        // @Service
        // @CacheConfig(cacheNames = "caffeineCacheManager")
        // public class UserInfoServiceImpl {

        //     /**
        //      * 模拟数据库存储数据
        //      */
        //     private HashMap<Integer, UserInfo> userInfoMap = new HashMap<>();

        //     @CachePut(key = "#userInfo.id")
        //     public void addUserInfo(UserInfo userInfo) {
        //         userInfoMap.put(userInfo.getId(), userInfo);
        //     }

        //     @Cacheable(key = "#id")
        //     public UserInfo getByName(Integer id) {
        //         return userInfoMap.get(id);
        //     }

        //     @CachePut(key = "#userInfo.id")
        //     public UserInfo updateUserInfo(UserInfo userInfo) {
        //         if (!userInfoMap.containsKey(userInfo.getId())) {
        //             return null;
        //         }
        //         // 取旧的值
        //         UserInfo oldUserInfo = userInfoMap.get(userInfo.getId());
        //         // 替换内容
        //         if (!StringUtils.isEmpty(oldUserInfo.getAge())) {
        //             oldUserInfo.setAge(userInfo.getAge());
        //         }
        //         if (!StringUtils.isEmpty(oldUserInfo.getName())) {
        //             oldUserInfo.setName(userInfo.getName());
        //         }
        //         if (!StringUtils.isEmpty(oldUserInfo.getSex())) {
        //             oldUserInfo.setSex(userInfo.getSex());
        //         }
        //         // 将新的对象存储，更新旧对象信息
        //         userInfoMap.put(oldUserInfo.getId(), oldUserInfo);
        //         // 返回新对象信息
        //         return oldUserInfo;
        //     }

        //     @CacheEvict(key = "#id")
        //     public void deleteById(Integer id) {
        //         userInfoMap.remove(id);
        //     }

        // }


        // -----Caffeine在Reactor的实战-----
        // Caffeine和Reactor的结合是通过CacheMono和CacheFlux来使用的，Caffine会存储一个Flux或Mono作为缓存的结果。
        // 首先定义Caffeine的缓存：
        // final Cache<String, String> caffeineCache = Caffeine.newBuilder()
        //         .expireAfterWrite(Duration.ofSeconds(30))
        //         .recordStats()
        //         .build();CacheMono

        // final Mono<String> cachedMonoCaffeine = CacheMono
        //     .lookup(
        //         k -> Mono.justOrEmpty(caffeineCache.getIfPresent(k)).map(Signal::next),
        //         key
        //     )
        //     .onCacheMissResume(this.handleCacheMiss(key))
        //     .andWriteWith((k, sig) -> Mono.fromRunnable(() ->
        //         caffeineCache.put(k, Objects.requireNonNull(sig.get()))
        //     ));
        // // lookup方法查询cache中是否已存在，如果不存在，则通过onCacheMissResume重新生成一个Mono，并通过andWriteWith方法将结果存入缓存中。

        // CacheFlux
        // final Flux<Integer> cachedFluxCaffeine = CacheFlux
        //     .lookup(
        //         k -> {
        //             final List<Integer> cached = caffeineCache.getIfPresent(k);
        
        //             if (cached == null) {
        //             return Mono.empty();
        //             }
        
        //             return Mono.just(cached)
        //                 .flatMapMany(Flux::fromIterable)
        //                 .map(Signal::next)
        //                 .collectList();
        //         },
        //         key
        //     )
        //     .onCacheMissResume(this.handleCacheMiss(key))
        //     .andWriteWith((k, sig) -> Mono.fromRunnable(() ->
        //         caffeineCache.put(
        //             k,
        //             sig.stream()
        //                 .filter(signal -> signal.getType() == SignalType.ON_NEXT)
        //                 .map(Signal::get)
        //                 .collect(Collectors.toList())
        //         )
        //     ));
        //         // 同理CacheFlux的用法也类似。
        // }
