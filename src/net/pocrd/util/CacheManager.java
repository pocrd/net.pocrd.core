package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private static CacheManager instatnce = new CacheManager();

    private CacheManager() {}

    public static CacheManager getSingleton() {
        return instatnce;
    }

    /**
     * 模拟cache
     */
    private static ConcurrentHashMap<String, Object> redis = new ConcurrentHashMap<String, Object>();

    public Object get(String key) {
        return redis.get(key);
    }

    public boolean set(String key, Object obj, int expire) {
        redis.put(key, obj);
        return true;
    }

    public boolean delete(String key) {
        redis.remove(key);
        return true;
    }
}
