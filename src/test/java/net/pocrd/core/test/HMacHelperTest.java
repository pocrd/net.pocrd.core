package net.pocrd.core.test;

import net.pocrd.util.HMacHelper;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 测试结论，对于HMacHelper这种，绝大部分时间都是同一个密钥在工作， 但是需要在多线程访问时进行同步的辅助类，使用ThreadLocal为每一个 线程缓存一个实例可以避免进行锁操作
 *
 * @author rendong
 */
public class HMacHelperTest {

    @Test
    public void testHMacHelper() {
        HMacHelper mac = new HMacHelper("0123456789");
        assertNotNull(mac);
    }

    @Test
    public void testVerify() throws UnsupportedEncodingException {
        HMacHelper mac1 = new HMacHelper("0123456789");
        assertNotNull(mac1);
        HMacHelper mac2 = new HMacHelper("0123456789");
        assertNotNull(mac2);
        assertArrayEquals(mac1.sign("11111111111111111111111111111".getBytes("UTF-8")), mac2.sign("11111111111111111111111111111".getBytes("UTF-8")));
    }

    @Test
    public void testMultiThread() throws UnsupportedEncodingException {
        final HMacHelper mac = new HMacHelper("0123456789");
        final byte[] result = mac.sign("11111111111111111111111111111".getBytes("UTF-8"));
        long s = System.currentTimeMillis();
        MultithreadTestHelper.runInMultithread(10, 30000, new Runnable() {
            @Override
            public void run() {
                HMacHelper m = new HMacHelper("0123456789");
                byte[] r = m.sign("11111111111111111111111111111".getBytes());
                assertArrayEquals(result, r);
            }
        });
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testPerformance() throws UnsupportedEncodingException {
        long s = System.currentTimeMillis();
        HMacHelper mac = new HMacHelper("0123456789");
        for (int i = 300000; i > 0; i--) {
            mac.sign("11111111111111111111111111111".getBytes("UTF-8"));
        }
        System.out.println(System.currentTimeMillis() - s);
        s = System.currentTimeMillis();
        for (int i = 300000; i > 0; i--) {

            HMacHelper m = new HMacHelper("0123456789");

            m.sign("11111111111111111111111111111".getBytes("UTF-8"));
        }
        System.out.println(System.currentTimeMillis() - s);
    }
}
