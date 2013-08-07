package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager4Memcache implements ICacheManager{
	private static ICacheManager instatnce=new CacheManager4Memcache();
	private CacheManager4Memcache(){
	}
	
	public static ICacheManager getSingleton(){
		return instatnce;
	}
	
	/**
	 * 模拟cache
	 */
	private static ConcurrentHashMap<String,Object> redis=new ConcurrentHashMap<String,Object>();
	
	@Override
	public Object get(String key) {
		return redis.get(key);
	}

	@Override
	public boolean set(String key, Object obj, int expire) {
		redis.put(key, obj);
		return true;
	}

	@Override
	public boolean delete(String key) {
		redis.remove(key);
		return true;
	}

}
