package net.pocrd.core.test;

import net.pocrd.define.ConstField;
import net.pocrd.util.Base64Util;
import net.pocrd.util.RsaHelper;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//TODO:add multi-thread testing
public class RsaHelperTest {

    @Test
    public void testRsaEncryptAndDecryps() throws UnsupportedEncodingException {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            keygen.initialize(1024, random);
            KeyPair kp = keygen.generateKeyPair();
            byte[] pub = kp.getPublic().getEncoded();
            byte[] pri = kp.getPrivate().getEncoded();
            RsaHelper rsa = new RsaHelper(pub, pri);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append(String.valueOf(i));
            }
            String content = sb.toString();
            byte[] secret = rsa.encrypt(content.getBytes(ConstField.UTF8));
            try {
                assertEquals(content, new String(rsa.decrypt(secret), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            assertEquals(content, new String(RsaHelper.decrypt(secret, pri), "UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRsaSignAndVerify() {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            try {
                random.setSeed("leorendong@hotmail.com".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            keygen.initialize(1024, random);
            KeyPair kp = keygen.generateKeyPair();
            byte[] pub = kp.getPublic().getEncoded();
            byte[] pri = kp.getPrivate().getEncoded();
            System.out.println("pub:  " + Base64Util.encodeToString(pub));
            System.out.println("pri:  " + Base64Util.encodeToString(pri));
            RsaHelper rsa = new RsaHelper(pub, pri);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append(String.valueOf(i));
            }
            byte[] data = sb.toString().getBytes(ConstField.UTF8);
            assertTrue(rsa.verify(rsa.sign(data), data));
            assertTrue(RsaHelper.verify(rsa.sign(data), data, pub));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void generateKeyPair() {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            try {
                random.setSeed("sfhaitao.xyz!".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            keygen.initialize(1024, random);
            KeyPair kp = keygen.generateKeyPair();
            byte[] pub = kp.getPublic().getEncoded();
            byte[] pri = kp.getPrivate().getEncoded();
            System.out.println("pub:  " + Base64Util.encodeToString(pub));
            System.out.println("pri:  " + Base64Util.encodeToString(pri));
            RsaHelper rsa = new RsaHelper(pub, pri);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append(String.valueOf(i));
            }
            byte[] data = sb.toString().getBytes(ConstField.UTF8);
            assertTrue(rsa.verify(rsa.sign(data), data));
            assertTrue(RsaHelper.verify(rsa.sign(data), data, pub));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
