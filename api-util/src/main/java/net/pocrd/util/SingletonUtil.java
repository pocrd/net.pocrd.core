package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Create and cache an single instance for input class.
 *
 * @param <T>
 *
 * @author rendong
 */
public class SingletonUtil {
    private static ConcurrentHashMap<Class<?>, Object> cache = new ConcurrentHashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    public static <T> T getSingleton(Class<T> clazz) {
        T instance = (T)cache.get(clazz);
        if (instance == null) {
            synchronized (cache) {
                instance = (T)cache.get(clazz);
                if (instance == null) {
                    instance = createSingleton(clazz);
                    cache.put(clazz, instance);
                }
            }
        }
        return instance;
    }

    private static <T> T createSingleton(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建单例失败。", e);
        }
    }
}
