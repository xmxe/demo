package com.xmxe.util.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheByGuava {

    private static final Logger log = LoggerFactory.getLogger(CacheByGuava.class);

    /** 缓存项最大数量 */
    private static final long GUAVA_CACHE_SIZE = 100000;

    /** 缓存时间：天 */
    private static final long GUAVA_CACHE_DAY = 10;

    /** 缓存操作对象 */
    private static LoadingCache<String, Object> GLOBAL_CACHE = null;

    static {
        try {
            GLOBAL_CACHE = loadCache(new CacheLoader<String, Object>() {
                @Override
                public String load(String key) throws Exception {
                    // 处理缓存键不存在缓存值时的处理逻辑
                    return "";
                }
            });
        } catch (Exception e) {
            log.error("初始化Guava Cache出错", e);
        }
    }

    /**
     * 全局缓存设置
     * 缓存项最大数量：100000缓存有效时间（天）：10
     */
    private static LoadingCache<String, Object> loadCache(CacheLoader<String, Object> cacheLoader) throws Exception {
        LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
                // 初始容量
                .initialCapacity(5)
                // 缓存池大小,在缓存项接近该大小时,Guava开始回收旧的缓存项
                .maximumSize(GUAVA_CACHE_SIZE)
                // 设置时间对象没有被读/写访问则对象从内存中删除(在另外的线程里面不定期维护)
                .expireAfterAccess(GUAVA_CACHE_DAY,TimeUnit.DAYS)
                // 设置时间对象没有被写访问则对象从内存中删除
                .expireAfterWrite(GUAVA_CACHE_DAY,TimeUnit.DAYS)
                // 移除监听器,缓存项被移除时会触发
                .removalListener(new RemovalListener<String, Object>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, Object> rn) {
                        // 逻辑操作
                    }
                })
                // 开启Guava Cache的统计功能
                .recordStats()
                // 过期时间
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build(cacheLoader);
        return cache;
    }

    /**
     * 设置缓存值 注:若已有该key值,则会先移除(会触发removalListener移除监听器),再添加
     */
    public static void put(String key, Object value) {
        try {
            GLOBAL_CACHE.put(key, value);
        } catch (Exception e) {
            log.error("设置缓存值出错", e);
        }
    }

    /**
     * 批量设置缓存值
     */
    public static void putAll(Map<String, Object> map) {
        try {
            GLOBAL_CACHE.putAll(map);
        } catch (Exception e) {
            log.error("批量设置缓存值出错", e);
        }
    }

    /**
     * 获取缓存值 注：如果键不存在值,将调用CacheLoader的load方法加载新值到该键中
     */
    public static Object get(String key) {
        Object token = null;
        try {
            token = GLOBAL_CACHE.get(key);
        } catch (Exception e) {
            log.error("获取缓存值出错", e);
        }
        return token;
    }

    /**
     * 移除缓存
     */
    public static void remove(String key) {
        try {
            GLOBAL_CACHE.invalidate(key);
        } catch (Exception e) {
            log.error("移除缓存出错", e);
        }
    }

    /**
     * 批量移除缓存
     */
    public static void removeAll(Iterable<String> keys) {
        try {
            GLOBAL_CACHE.invalidateAll(keys);
        } catch (Exception e) {
            log.error("批量移除缓存出错", e);
        }
    }

    /**
     * 清空所有缓存
     */
    public static void removeAll() {
        try {
            GLOBAL_CACHE.invalidateAll();
        } catch (Exception e) {
            log.error("清空所有缓存出错", e);
        }
    }

    /**
     * 获取缓存项数量
     */
    public static long size() {
        long size = 0;
        try {
            size = GLOBAL_CACHE.size();
        } catch (Exception e) {
            log.error("获取缓存项数量出错", e);
        }
        return size;
    }

    /**
     * 获取缓存状态
     * 
     */
    public static CacheStats cacheStats() {
        return GLOBAL_CACHE == null ? null : GLOBAL_CACHE.stats();
    }
}
