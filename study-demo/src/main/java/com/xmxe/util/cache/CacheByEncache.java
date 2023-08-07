package com.xmxe.util.cache;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;

/**
 * Ehcache是一个纯java的进程内缓存框架,具有快速、精干的特点。是hibernate默认的cacheprovider。
 * 优点：支持多种缓存淘汰算法,包括LFU,LRU和FIFO；缓存支持堆内缓存,堆外缓存和磁盘缓存；支持多种集群方案,解决数据共享问题。
 * 缺点：性能比Caffeine差
 */
/**
 * 自定义过期策略实现
 */
public class CacheByEncache<K, V> implements ExpiryPolicy<K, V> {

    private final Map<K, Duration> keyExpireMap = new ConcurrentHashMap<>();


    public Duration setExpire(K key, Duration duration) {
        return keyExpireMap.put(key, duration);
    }

    public Duration getExpireByKey(K key) {
        return Optional.ofNullable(keyExpireMap.get(key))
                .orElse(null);
    }

    public Duration removeExpire(K key) {
        return keyExpireMap.remove(key);
    }

    @Override
    public Duration getExpiryForCreation(K key, V value) {
        return Optional.ofNullable(getExpireByKey(key))
                .orElse(Duration.ofNanos(Long.MAX_VALUE));
    }

    @Override
    public Duration getExpiryForAccess(K key, Supplier<? extends V> value) {
        return getExpireByKey(key);
    }

    @Override
    public Duration getExpiryForUpdate(K key, Supplier<? extends V> oldValue, V newValue) {
        return getExpireByKey(key);
    }


    public void main1(String[] args) throws Exception {
        // 声明一个cacheBuilder
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("encacheInstance", CacheConfigurationBuilder
                        // 声明一个容量为20的堆内缓存
                        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(20)))
                .build(true);
        // 获取Cache实例
        Cache<String, String> myCache = cacheManager.getCache("encacheInstance", String.class, String.class);
        // 写缓存
        myCache.put("key", "v");
        // 读缓存
        String value = myCache.get("key");
        // 移除换粗
        cacheManager.removeCache("myCache");
        cacheManager.close();
    }

    public static void main(String[] args) {
        String userCache = "userCache";

        // 自定义过期策略
        CacheByEncache<Object, Object> customExpiryPolicy = new CacheByEncache<>();
    
        // 声明一个容量为20的堆内缓存配置
        CacheConfigurationBuilder<String,String> configurationBuilder = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(20))
                .withExpiry(customExpiryPolicy);
    
        // 初始化一个缓存管理器
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                // 创建cache实例
                .withCache(userCache, configurationBuilder)
                .build(true);
    
        // 获取cache实例
        Cache<String, String> cache = cacheManager.getCache(userCache, String.class, String.class);
        // 获取过期策略
        CacheByEncache<String,String> expiryPolicy = (CacheByEncache<String,String>)cache.getRuntimeConfiguration().getExpiryPolicy();
    
        // 写入缓存数据
        cache.put("userName", "张三");
        // 设置3秒过期
        expiryPolicy.setExpire("userName", Duration.ofSeconds(3));
    
        // 读取缓存数据
        String value1 = cache.get("userName");
        System.out.println("第一次查询结果：" + value1);
    
        // 停顿4秒
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        // 读取缓存数据
        String value2 = cache.get("userName");
        System.out.println("第二次查询结果：" + value2);
    }
}
