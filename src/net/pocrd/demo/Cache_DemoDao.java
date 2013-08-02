package net.pocrd.demo;

import java.util.List;

import net.pocrd.util.CacheManager4Redis;
import net.pocrd.util.StringHelper;

public class Cache_DemoDao extends DemoDao {
	public int[] getDemoEntity(double paramDouble, long paramLong) {
		String str = "v1.0|getDemoEntity|int[]|" + paramDouble + "|"
				+ paramLong + "|";
		CacheManager4Redis localCacheManager4Redis = CacheManager4Redis
				.getSingleton();
		Object localObject = localCacheManager4Redis.get(str);
		if (localObject == null) {
			int[] arrayOfInt = super.getDemoEntity(paramDouble, paramLong);
			if (arrayOfInt != null) {
				localCacheManager4Redis.set(str, arrayOfInt, 100);
				return arrayOfInt;
			}
			return null;
		}
		if (localObject instanceof int[])
			return ((int[]) localObject);
		throw new RuntimeException("Cache object conflict,key:" + str);
	}

	public List getDemoEntity(List paramList) {
		String str = "v1.0|getDemoEntity|java.util.List|" + paramList + "|";
		CacheManager4Redis localCacheManager4Redis = CacheManager4Redis
				.getSingleton();
		Object localObject = localCacheManager4Redis.get(str);
		if (localObject == null) {
			List localList = super.getDemoEntity(paramList);
			if (localList != null) {
				localCacheManager4Redis.set(str, localList, 100);
				return localList;
			}
			return null;
		}
		if (localObject instanceof List)
			return ((List) localObject);
		throw new RuntimeException("Cache object conflict,key:" + str);
	}

	public int getDemoEntity(int paramInt) {
		String str = "v1.0|getDemoEntity|int|" + paramInt + "|";
		CacheManager4Redis localCacheManager4Redis = CacheManager4Redis
				.getSingleton();
		Object localObject = localCacheManager4Redis.get(str);
		if (localObject == null) {
			int i = super.getDemoEntity(paramInt);
			localCacheManager4Redis.set(str, Integer.valueOf(i), 100);
			return i;
		}
		if (localObject instanceof Integer)
			return ((Integer) localObject).intValue();
		throw new RuntimeException("Cache object conflict,key:" + str);
	}

	public DemoEntity getDemoEntity(int paramInt, String paramString,
			boolean paramBoolean, byte paramByte, short paramShort,
			char paramChar, long paramLong, float paramFloat, double paramDouble) {
		String str = "v1.0|getDemoEntity|net.pocrd.demo.DemoEntity|" + paramInt
				+ "|" + paramString + "|" + paramBoolean + "|" + paramByte
				+ "|" + paramShort + "|" + paramChar + "|" + paramLong + "|"
				+ paramFloat + "|" + paramDouble + "|";
		CacheManager4Redis localCacheManager4Redis = CacheManager4Redis
				.getSingleton();
		Object localObject = localCacheManager4Redis.get(str);
		if (localObject == null) {
			DemoEntity localDemoEntity = super.getDemoEntity(paramInt,
					paramString, paramBoolean, paramByte, paramShort,
					paramChar, paramLong, paramFloat, paramDouble);
			if (localDemoEntity != null) {
				localCacheManager4Redis.set(str, localDemoEntity, 100);
				return localDemoEntity;
			}
			return null;
		}
		if (localObject instanceof DemoEntity)
			return ((DemoEntity) localObject);
		throw new RuntimeException("Cache object conflict,key:" + str);
	}

	public int[] getDemoEntity(int[] paramArrayOfInt,
			DemoEntity[] paramArrayOfDemoEntity, DemoDao.Test paramTest,
			int paramInt) {
		String str = "v1.0|getDemoEntity|int[]|"
				+ StringHelper.toString(paramArrayOfInt) + "|"
				+ StringHelper.toString(paramArrayOfDemoEntity) + "|"
				+ paramTest + "|" + paramInt + "|";
		CacheManager4Redis localCacheManager4Redis = CacheManager4Redis
				.getSingleton();
		Object localObject = localCacheManager4Redis.get(str);
		if (localObject == null) {
			int[] arrayOfInt = super.getDemoEntity(paramArrayOfInt,
					paramArrayOfDemoEntity, paramTest, paramInt);
			if (arrayOfInt != null) {
				localCacheManager4Redis.set(str, arrayOfInt, 100);
				return arrayOfInt;
			}
			return null;
		}
		if (localObject instanceof int[])
			return ((int[]) localObject);
		throw new RuntimeException("Cache object conflict,key:" + str);
	}
}