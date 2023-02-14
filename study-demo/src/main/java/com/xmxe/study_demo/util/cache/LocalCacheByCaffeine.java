package com.xmxe.study_demo.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * 看了阿里大佬用的本地缓存，那叫一个优雅！https://mp.weixin.qq.com/s/ppOCka7k-YpPI3V5Ui--IA
 */
public class LocalCacheByCaffeine {
    
    static class LRUCache extends LinkedHashMap {

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
        public Object get(Object key) {
            readLock.lock();
            try {
                return super.get(key);
            } finally {
                readLock.unlock();
            }
        }
    
        @Override
        public Object put(Object key, Object value) {
            writeLock.lock();
            try {
                return super.put(key, value);
            } finally {
                writeLock.unlock();
            }
        }
    
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > maxSize;
        }
    }

    // static class CaffeineCacheTest {

    //     public static void main(String[] args) throws Exception {
    //         //创建guava cache
    //         Cache<String, String> loadingCache = Caffeine.newBuilder()
    //                 //cache的初始容量
    //                 .initialCapacity(5)
    //                 //cache最大缓存数
    //                 .maximumSize(10)
    //                 //设置写缓存后n秒钟过期
    //                 .expireAfterWrite(17, TimeUnit.SECONDS)
    //                 //设置读写缓存后n秒钟过期,实际很少用到,类似于expireAfterWrite
    //                 //.expireAfterAccess(17, TimeUnit.SECONDS)
    //                 .build();
    //         String key = "key";
    //         // 往缓存写数据
    //         loadingCache.put(key, "v");

    //         // 获取value的值，如果key不存在，获取value后再返回
    //         String value = loadingCache.get(key, CaffeineCacheTest::getValueFromDB);

    //         // 删除key
    //         loadingCache.invalidate(key);
    //     }

    //     private static String getValueFromDB(String key) {
    //         return "v";
    //     }
    // }

    static class EncacheTest {

        public static void main(String[] args) throws Exception {
            // 声明一个cacheBuilder
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                    .withCache("encacheInstance", CacheConfigurationBuilder
                            //声明一个容量为20的堆内缓存
                            .newCacheConfigurationBuilder(String.class,String.class, ResourcePoolsBuilder.heap(20)))
                    .build(true);
            // 获取Cache实例
            Cache<String,String> myCache =  cacheManager.getCache("encacheInstance", String.class, String.class);
            // 写缓存
            myCache.put("key","v");
            // 读缓存
            String value = myCache.get("key");
            // 移除换粗
            cacheManager.removeCache("myCache");
            cacheManager.close();
        }
    }

}
