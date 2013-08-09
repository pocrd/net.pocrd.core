package net.pocrd.util;

public interface ICacheManager {
    public Object get(String key);

    public boolean set(String key, Object obj, int expire);

    public boolean delete(String key);
}
