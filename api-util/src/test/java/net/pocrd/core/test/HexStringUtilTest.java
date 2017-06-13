package net.pocrd.core.test;

import net.pocrd.define.ConstField;
import net.pocrd.util.HexStringUtil;
import net.pocrd.util.Md5Util;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class HexStringUtilTest {

    @Test
    public void testHexStringUtil() {
        assertEquals("9433e6ece1ff143424623393dafd4413", HexStringUtil.toHexString(HexStringUtil.toByteArray("9433e6ece1ff143424623393dafd4413")));
    }

    @Test
    public void testRandomTest() {
        for (int i = 0; i < 100000; i++) {
            byte[] bs = Md5Util.compute(String.valueOf(new Random().nextLong()).getBytes(ConstField.UTF8));
            Arrays.equals(bs, HexStringUtil.toByteArray(HexStringUtil.toHexString(bs)));
        }
    }
}
