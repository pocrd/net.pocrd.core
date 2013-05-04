package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Create and cache an object, which is a subclass instance of input class,
 * this subclass provide cache function for all method in input class which
 * has the CacheMethod annotation.
 * 
 * @author rendong
 * @param <T>
 */
public class CacheProvider {
    private static ConcurrentHashMap<Class<?>, Object> cache = new ConcurrentHashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    public static <T> T getCahceable(Class<T> clazz) {
        T instance = (T)cache.get(clazz);
        if (instance == null) {
            synchronized (cache) {
                instance = (T)cache.get(clazz);
                if (instance == null) {
                    instance = getCacheInstance(clazz);
                    cache.put(clazz, instance);
                }
            }
        }
        return instance;
    }

    private static <T> T getCacheInstance(Class<T> clazz) {
        return null;
    }
}
