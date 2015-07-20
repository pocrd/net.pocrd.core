package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

class CacheManager4Memcache implements ICacheManager {
    //    private static ICacheManager instance = new CacheManager4Memcache();
    //
    //    private CacheManager4Memcache() {}
    //
    //    public static ICacheManager getSingleton() {
    //        return instance;
    //    }

    /**
     * 模拟cache
     */
    private static ConcurrentHashMap<String, Object> memcache = new ConcurrentHashMap<String, Object>();

    public Object get(String key) {
        return memcache.get(key);
    }

    public boolean set(String key, Object obj, int expire) {
        memcache.put(key, obj);
        return true;
    }

    public boolean delete(String key) {
        memcache.remove(key);
        return true;
    }

}
