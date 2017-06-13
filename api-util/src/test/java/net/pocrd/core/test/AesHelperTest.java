package net.pocrd.core.test;

import net.pocrd.define.ConstField;
import net.pocrd.util.AesHelper;
import net.pocrd.util.Base64Util;
import net.pocrd.util.HexStringUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

//TODO:add multi-thread    testing
public class AesHelperTest {

    @Test
    public void testAesHelper() {
        byte[] key = AesHelper.randomKey(256);
        AesHelper aes = new AesHelper(key, true);
        assertTrue(aes != null);
        for (int i = 0; i < 10; i++) {
            System.out.println(HexStringUtil.toHexString(aes.encrypt("12345678".getBytes())));
        }
        System.out.println(Base64Util.encodeToString(key));
        AesHelper h = new AesHelper(Base64Util.decode("bY7813NzNt548KAC4QI+PwpK1khDkWPQC+SHbT1njRs="), true);
        System.out.println(HexStringUtil.toHexString(h.encrypt("2683".getBytes(ConstField.UTF8))));
        System.out.println(Long.parseLong(new String(h.decrypt(HexStringUtil.toByteArray("f1bb1ff3")), ConstField.UTF8)));

        try {
            String s = "123u4uuuyt人生就是一颗菠菜56ed7c8v90b98.76|54321";
            System.out.println(s.getBytes("GBK").length);
            System.out.println(s.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEncrypt() throws UnsupportedEncodingException {
        {
            // remember to replace {java_home}/jre/lib/security with
            // http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
            byte[] key = AesHelper.randomKey(256);
            AesHelper aes = new AesHelper(key, true);
            assertTrue(aes != null);
            StringBuilder sb = new StringBuilder(1000);
            for (int i = 0; i < 100; i++) {
                sb.append(i);
            }
            byte[] content = sb.toString().getBytes(ConstField.UTF8);
            byte[] bs = aes.encrypt(content);
            assertTrue(bs.length == content.length);
        }

        {
            byte[] key = AesHelper.randomKey(256);
            AesHelper aes = new AesHelper(key, "0123456789123456".getBytes("UTF-8"));
            assertTrue(aes != null);
            StringBuilder sb = new StringBuilder(1000);
            for (int i = 0; i < 100; i++) {
                sb.append(i);
            }
            byte[] content = sb.toString().getBytes(ConstField.UTF8);
            byte[] bs = aes.encrypt(content);
            assertTrue(bs.length == ((content.length + 15) / 16) * 16);
        }
    }

    @Test
    public void testDecrypt() throws UnsupportedEncodingException {
        {
            // remember to replace {java_home}/jre/lib/security with
            // http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
            byte[] key = AesHelper.randomKey(256);
            AesHelper aes = new AesHelper(key, true);
            assertTrue(aes != null);
            StringBuilder sb = new StringBuilder(1000);
            for (int i = 0; i < 1000; i++) {
                sb.append(i);
            }
            byte[] content = sb.toString().getBytes(ConstField.UTF8);
            byte[] bs = aes.encrypt(content);
            assertTrue(bs.length == content.length);
            byte[] c2 = aes.decrypt(bs);
            assertTrue(Arrays.equals(content, c2));
        }

        {
            byte[] key = AesHelper.randomKey(256);
            AesHelper aes = new AesHelper(key, "0123456789123456".getBytes("UTF-8"));
            assertTrue(aes != null);
            StringBuilder sb = new StringBuilder(1000);
            for (int i = 0; i < 1000; i++) {
                sb.append(i);
            }
            byte[] content = sb.toString().getBytes(ConstField.UTF8);
            byte[] bs = aes.encrypt(content);
            assertTrue(bs.length == ((content.length + 15) / 16) * 16);
            byte[] c2 = aes.decrypt(bs);
            assertTrue(Arrays.equals(content, c2));
        }
    }

    @Test
    public void testRandomKey() {
        byte[] key128 = AesHelper.randomKey(128);
        assertTrue(key128.length == 128 / 8);
        byte[] key192 = AesHelper.randomKey(192);
        assertTrue(key192.length == 192 / 8);
        byte[] key256 = AesHelper.randomKey(256);
        assertTrue(key256.length == 256 / 8);
    }

    @Test
    public void testMultithread() throws UnsupportedEncodingException {
        byte[] key = AesHelper.randomKey(256);
        System.out.println("aes:  " + Base64Util.encodeToString(key));
        final AesHelper aes = new AesHelper(key, "0123456789123456".getBytes("UTF-8"));
        assertTrue(aes != null);
        StringBuilder sb = new StringBuilder(1000);
        for (int i = 0; i < 1000; i++) {
            sb.append(i);
        }
        final byte[] content = sb.toString().getBytes("UTF-8");
        MultithreadTestHelper.runInMultithread(5, 10000, new Runnable() {

            @Override
            public void run() {
                byte[] bs = aes.encrypt(content);
                assertTrue(bs.length == ((content.length + 15) / 16) * 16);
                byte[] c2 = aes.decrypt(bs);
                assertTrue(Arrays.equals(content, c2));
            }
        });
    }
}
