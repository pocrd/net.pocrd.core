package net.pocrd.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import net.pocrd.define.ConstField;
import net.pocrd.util.RsaHelper;

import org.junit.Test;

//TODO:add multi-thread testing
public class RsaHelperTest {

    @Test
    public void testRsaEncryptAndDecryps() {
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
            for (int i = 0; i < 1000; i++) {
                sb.append(String.valueOf(i));
            }
            String content = sb.toString();
            byte[] secret = rsa.encrypt(content.getBytes(ConstField.UTF8));
            assertEquals(content, new String(rsa.decrypt(secret)));
            assertEquals(content, new String(RsaHelper.decrypt(secret, pri)));

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
            random.setSeed("leorendong@hotmail.com".getBytes());
            keygen.initialize(1024, random);
            KeyPair kp = keygen.generateKeyPair();
            byte[] pub = kp.getPublic().getEncoded();
            byte[] pri = kp.getPrivate().getEncoded();

            RsaHelper rsa = new RsaHelper(pub, pri);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
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
