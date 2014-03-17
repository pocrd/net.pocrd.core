package net.pocrd.core.test;

import net.pocrd.core.test.DemoDao.TestEnum;
import net.pocrd.util.CacheProvider;

import org.junit.Test;

public class CacheProviderTest {
    @Test
    public void Test(){        
        MultithreadTestHelper.runInMultithread(3, 10, new Runnable() {
            @Override
            public void run() {
                    long start = System.currentTimeMillis();
                    DemoDao d = CacheProvider.getSingleton(DemoDao.class);
                    int[] i = new int[2];
                    d.getDemoEntity(i, null, TestEnum.a, 0);
                    try {
                        d.getDemoEntity(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    d.getDemoEntity(0, 0);
                    d.getDemoEntity(0, null, true, (byte) 0, (short) 0, '0', 0, 0, 0);
                    d.getDemoEntity(null);
                    System.out.println("cost time:"
                            + (System.currentTimeMillis() - start));
            }
        });
    }

}
