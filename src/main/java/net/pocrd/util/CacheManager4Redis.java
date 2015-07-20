package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

class CacheManager4Redis implements ICacheManager {
    //    private static ICacheManager instance = new CacheManager4Redis();
    //
    //    private CacheManager4Redis() {}
    //
    //    public static ICacheManager getSingleton() {
    //        return instance;
    //    }

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
