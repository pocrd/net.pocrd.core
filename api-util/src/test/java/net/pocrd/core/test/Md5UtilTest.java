package net.pocrd.core.test;

import net.pocrd.util.Md5Util;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class Md5UtilTest {

    @Test
    public void testCompute() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(1000);
        for (int i = 0; i < 1000; i++) {
            sb.append(i);
        }
        byte[] bs = Md5Util.compute(sb.toString().getBytes("UTF-8"));
        assertTrue(bs.length == 16);
        Arrays.equals(bs, Md5Util.compute(sb.toString().getBytes("UTF-8")));
    }

}
