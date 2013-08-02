package net.pocrd.demo;

import net.pocrd.demo.DemoDao.Test;
import net.pocrd.util.CacheProvider;

public class CacheTest extends Thread {
	private String name;

	public CacheTest(String name) {
		this.name = "Threa" + name;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		for (int j = 0; j < 10; j++) {
			long start = System.currentTimeMillis();
			DemoDao<?> d = CacheProvider.getSingleton(DemoDao.class);
			int[] i = new int[2];
			d.getDemoEntity(i, null, Test.a, 0);
			d.getDemoEntity(0);
			d.getDemoEntity(0, 0);
			d.getDemoEntity(0, null, true, (byte) 0, (short) 0, '0', 0, 0, 0);
			d.getDemoEntity(null);
			System.out.println(this.name + " DemoDao: " + j + " cost time:"
					+ (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			DemoDaoGen d2 = CacheProvider.getSingleton(DemoDaoGen.class);
			d2.getDemoEntity(i, null, Test.a, 0);
			d2.getDemoEntity(0);
			d2.getDemoEntity(0, 0);
			d2.getDemoEntity(0, null, true, (byte) 0, (short) 0, '0', 0, 0, 0);
			d2.getDemoEntity(null);
			System.out.println(this.name + " DemoDaoGen: " + j + " cost time:"
					+ (System.currentTimeMillis() - start));
		}
	}

	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException, Exception {
		// DemoDao<?> d = GenCachedClass(DemoDao.class);
		CacheTest[] thread = new CacheTest[5];
		thread[0] = new CacheTest("1");
		thread[1] = new CacheTest("2");
		thread[2] = new CacheTest("3");
		thread[3] = new CacheTest("4");
		thread[4] = new CacheTest("5");
		thread[0].start();
		thread[1].start();
		thread[2].start();
		thread[3].start();
		thread[4].start();
	}
}
