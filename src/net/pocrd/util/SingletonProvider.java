package net.pocrd.util;

import java.util.HashMap;

/**
 * Create and cache an single instance for input class.
 * 
 * @author rendong
 * @param <T>
 */
public class SingletonProvider {
    private static HashMap<Class<?>, Object> cache = new HashMap<Class<?>, Object>();

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
        return null;
    }
}
