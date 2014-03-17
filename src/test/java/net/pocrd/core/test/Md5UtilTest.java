package net.pocrd.core.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import net.pocrd.util.Md5Util;

import org.junit.Test;

public class Md5UtilTest {

    @Test
    public void testCompute() {
        StringBuilder sb = new StringBuilder(1000);
        for (int i = 0; i < 1000; i++) {
            sb.append(i);
        }
        byte[] bs = Md5Util.compute(sb.toString().getBytes());
        assertTrue(bs.length == 16);
        Arrays.equals(bs, Md5Util.compute(sb.toString().getBytes()));
    }

}
