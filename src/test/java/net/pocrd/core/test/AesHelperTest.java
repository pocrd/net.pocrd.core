package net.pocrd.core.test;

import net.pocrd.define.ConstField;
import net.pocrd.util.AesHelper;
import net.pocrd.util.Base64Util;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

//TODO:add multi-thread    testing
public class AesHelperTest {

    @Test
    public void testAesHelper() {
        byte[] key = AesHelper.randomKey(256);
        AesHelper aes = new AesHelper(key, null);
        assertTrue(aes != null);
        System.out.println(Base64Util.encodeToString(key));
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
