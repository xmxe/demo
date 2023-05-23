package com.xmxe.study_demo.util.cache;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 采用ConcurrentHashMap作为缓存数据存储服务，然后开启一个定时调度，每隔500毫秒检查一下过期的缓存数据，然后清除掉
 */
public class CacheCustom {

    /**
     * 缓存数据
     */
    private final static Map<String, CacheEntity> CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 定时器线程池，用于清除过期缓存
     */
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    static {
        // 注册一个定时线程任务，服务启动1秒之后，每隔500毫秒执行一次
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // 清理过期缓存
                clearCache();
            }
        }, 1000, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * 添加缓存
     * 
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void put(String key, Object value) {
        put(key, value, 0);
    }

    /**
     * 添加缓存
     * 
     * @param key    缓存键
     * @param value  缓存值
     * @param expire 缓存时间，单位秒
     */
    public static void put(String key, Object value, long expire) {
        CacheEntity cacheEntity = new CacheEntity();
        cacheEntity.setKey(key);
        cacheEntity.setValue(value);
        if (expire > 0) {
            Long expireTime = System.currentTimeMillis() + Duration.ofSeconds(expire).toMillis();
            cacheEntity.setExpireTime(expireTime);
        }
        CACHE_MAP.put(key, cacheEntity);
    }

    /**
     * 获取缓存
     * 
     * @param key
     * @return
     */
    public static Object get(String key) {
        if (CACHE_MAP.containsKey(key)) {
            return CACHE_MAP.get(key).getValue();
        }
        return null;
    }

    /**
     * 移除缓存
     * 
     * @param key
     */
    public static void remove(String key) {
        if (CACHE_MAP.containsKey(key)) {
            CACHE_MAP.remove(key);
        }
    }

    /**
     * 清理过期的缓存数据
     */
    private static void clearCache() {
        if (CACHE_MAP.size() > 0) {
            return;
        }
        Iterator<Map.Entry<String, CacheEntity>> iterator = CACHE_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheEntity> entry = iterator.next();
            if (entry.getValue().getExpireTime() != null
                    && entry.getValue().getExpireTime().longValue() > System.currentTimeMillis()) {
                iterator.remove();
            }
        }
    }

}

class CacheEntity {

    /**
     * 缓存键
     */
    private String key;

    /**
     * 缓存值
     */
    private Object value;

    /**
     * 过期时间
     */
    private Long expireTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
    
}
