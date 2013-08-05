package net.pocrd.util;


/**
 * 缓存管理接口，具体可使用memcahe或者redis...
 * @author guankaiqiang
 *
 */
public interface ICacheManager {
	public Object get(String key);
	public boolean set(String key,Object obj,int expire);
	public boolean delete(String key);
}
