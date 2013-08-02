package net.pocrd.demo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.pocrd.util.CacheManager4Redis;
import net.pocrd.util.ICacheManager;

/**
 * @author guankaiqiang
 * 
 */
public class DemoDaoGen extends DemoDao {
	@Override
	public DemoEntity getDemoEntity(int condition, String condition2,
			boolean conditionBool, byte condtionByte, short condtionShort,
			char condtionChar, long condtionLong, float condtionFloat,
			double condtionDouble) {
		String cachekey = "getDemoEntity|" + condition + "|" + condition2
				+ conditionBool + condtionByte + condtionShort + condtionChar
				+ condtionLong + condtionFloat + condtionDouble;
		ICacheManager cacheManager = CacheManager4Redis.getSingleton();
		Object obj = cacheManager.get(cachekey);
		// unbox return;
		if (obj == null) {
			DemoEntity entity = super.getDemoEntity(condition, condition2,
					conditionBool, condtionByte, condtionShort, condtionChar,
					condtionLong, condtionFloat, condtionDouble);
			if (entity != null) {
				cacheManager.set(cachekey, entity, 100);
				return entity;
			} else
				return null;
		} else {
			if (obj instanceof DemoEntity) {
				return (DemoEntity) obj;
			} else
				throw new RuntimeException("Cache object conflict,key:"
						+ cachekey);
		}
	}

	@Override
	public int[] getDemoEntity(int[] condition, DemoEntity[] obj, Test test,
			int valueType) {
		return super.getDemoEntity(condition, obj, test, valueType);
	}

	@Override
	public int[] getDemoEntity(double conditionDouble, long conditionlong) {
		// return super.getDemoEntity(conditionDouble, conditionlong);
		String cachekey = new StringBuilder("getDemoEntity|")
				.append(conditionDouble).append("|").append(conditionlong)
				.toString();
		ICacheManager cacheManager = CacheManager4Redis.getSingleton();
		Object obj = cacheManager.get(cachekey);
		if (obj == null) {
			int[] entity = super.getDemoEntity(conditionDouble, conditionlong);
			if (entity != null) {
				cacheManager.set(cachekey, entity, 100);
				return entity;
			} else
				return null;
		} else {
			return null;
		}
	}

	@Override
	public int getDemoEntity(int condition) {
		String cachekey = "getDemoEntity|" + condition + "|";
		ICacheManager cacheManager = CacheManager4Redis.getSingleton();
		Object obj = cacheManager.get(cachekey);
		if (obj == null) {
			int entity = super.getDemoEntity(condition);
			cacheManager.set(cachekey, entity, 100);
			return entity;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss E");
		Calendar calendar = Calendar.getInstance();
		System.out.println(dateformat.format(calendar.getTime()));
		calendar.add(Calendar.SECOND, 3600);
		System.out.println(dateformat.format(calendar.getTime()));
	}
	public void t(){
	}
}
