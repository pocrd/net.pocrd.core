package net.pocrd.core.test;

import net.pocrd.util.WebRequestUtil;
import org.junit.Test;

/**
 * Created by rendong on 15/1/29.
 */
public class WebRequestUtilTest {
    @Test
    public void testConnectionPoolTimeoutException() {
        int N = 51;
        Thread[] ts = new Thread[N];
        for (int i = 0; i < N; ) {
            ts[i] = new Thread(new Runnable() {
                @Override public void run() {
                    String s = WebRequestUtil.getResponseString("http://www.baid1u.com", "");
                }
            });
            i++;
        }

        for (int i = 0; i < N; ) {
            ts[i].start();
            i++;
        }
        for (int i = 0; i < N; ) {
            try {
                ts[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
