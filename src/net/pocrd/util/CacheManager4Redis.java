package net.pocrd.util;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager4Redis implements ICacheManager{
	private static CacheManager4Redis instatnce=new CacheManager4Redis();
	private CacheManager4Redis(){
	}
	
	public static CacheManager4Redis getSingleton(){
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
