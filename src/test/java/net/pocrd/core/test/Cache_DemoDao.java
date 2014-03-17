package net.pocrd.core.test;

import java.util.List;

import net.pocrd.util.CacheProvider;
import net.pocrd.util.CacheProvider.CacheKeyUtil;
import net.pocrd.util.ICacheManager;

public class Cache_DemoDao extends DemoDao {
    public DemoEntity getDemoEntity(int paramInt, String paramString, boolean paramBoolean, byte paramByte, short paramShort, char paramChar,
            long paramLong, float paramFloat, double paramDouble) {
        String str = "v1.0|getDemoEntity|" + paramInt + "|" + paramString + "|" + paramBoolean + "|" + paramByte + "|" + paramShort + "|" + paramChar
                + "|" + paramLong + "|" + paramFloat + "|" + paramDouble + "|";
        System.out.println(str);
        ICacheManager localICacheManager = CacheProvider.getCacheManager();
        Object localObject = localICacheManager.get(str);
        if (localObject == null) {
            DemoEntity localDemoEntity = super.getDemoEntity(paramInt, paramString, paramBoolean, paramByte, paramShort, paramChar, paramLong,
                    paramFloat, paramDouble);
            if (localDemoEntity != null) {
                localICacheManager.set(str, localDemoEntity, 100);
                return localDemoEntity;
            }
            return null;
        }
        if (localObject instanceof DemoEntity) return ((DemoEntity)localObject);
        throw new RuntimeException("Cache object conflict,key:" + str);
    }

    public int getDemoEntity(int paramInt) throws Exception {
        String str = "v1.0|getDemoEntity|" + paramInt + "|";
        System.out.println(str);
        ICacheManager localICacheManager = CacheProvider.getCacheManager();
        Object localObject = localICacheManager.get(str);
        if (localObject == null) {
            int i = super.getDemoEntity(paramInt);
            localICacheManager.set(str, Integer.valueOf(i), 100);
            return i;
        }
        if (localObject instanceof Integer) return ((Integer)localObject).intValue();
        throw new RuntimeException("Cache object conflict,key:" + str);
    }

    public int[] getDemoEntity(int[] paramArrayOfInt, DemoEntity[] paramArrayOfDemoEntity, DemoDao.TestEnum paramTestEnum, int paramInt) {
        String str = "v1.0|getDemoEntity|" + CacheKeyUtil.toString(paramArrayOfInt) + "|" + CacheKeyUtil.toString(paramArrayOfDemoEntity) + "|"
                + paramTestEnum + "|" + paramInt + "|";
        System.out.println(str);
        ICacheManager localICacheManager = CacheProvider.getCacheManager();
        Object localObject = localICacheManager.get(str);
        if (localObject == null) {
            int[] arrayOfInt = super.getDemoEntity(paramArrayOfInt, paramArrayOfDemoEntity, paramTestEnum, paramInt);
            if (arrayOfInt != null) {
                localICacheManager.set(str, arrayOfInt, 100);
                return arrayOfInt;
            }
            return null;
        }
        if (localObject instanceof int[]) return ((int[])localObject);
        throw new RuntimeException("Cache object conflict,key:" + str);
    }

    public int[] getDemoEntity(double paramDouble, long paramLong) {
        String str = "v1.0|getDemoEntity|" + paramDouble + "|" + paramLong + "|";
        System.out.println(str);
        ICacheManager localICacheManager = CacheProvider.getCacheManager();
        Object localObject = localICacheManager.get(str);
        if (localObject == null) {
            int[] arrayOfInt = super.getDemoEntity(paramDouble, paramLong);
            if (arrayOfInt != null) {
                localICacheManager.set(str, arrayOfInt, 100);
                return arrayOfInt;
            }
            return null;
        }
        if (localObject instanceof int[]) return ((int[])localObject);
        throw new RuntimeException("Cache object conflict,key:" + str);
    }

    @SuppressWarnings("unchecked")
    public List<String> getDemoEntity(List<String> paramList) {
        String str = "v1.0|getDemoEntity|" + paramList + "|";
        System.out.println(str);
        ICacheManager localICacheManager = CacheProvider.getCacheManager();
        Object localObject = localICacheManager.get(str);
        if (localObject == null) {
            List<String> localList = super.getDemoEntity(paramList);
            if (localList != null) {
                localICacheManager.set(str, localList, 100);
                return localList;
            }
            return null;
        }
        if (localObject instanceof List) return ((List<String>)localObject);
        throw new RuntimeException("Cache object conflict,key:" + str);
    }
}