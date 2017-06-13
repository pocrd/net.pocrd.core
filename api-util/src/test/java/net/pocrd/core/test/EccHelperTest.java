package net.pocrd.core.test;

import net.pocrd.define.ConstField;
import net.pocrd.util.EccHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EccHelperTest {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testEccEncryptAndDecryps() {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("EC", "BC");
            keygen.initialize(192, SecureRandom.getInstance("SHA1PRNG"));
            KeyPair kp = keygen.generateKeyPair();
            byte[] pub = kp.getPublic().getEncoded();
            byte[] pri = kp.getPrivate().getEncoded();
            EccHelper ecc = new EccHelper(pub, pri);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append(String.valueOf(i));
            }
            String content = sb.toString();
            byte[] secret = ecc.encrypt(content.getBytes(ConstField.UTF8));
            assertEquals(content, new String(ecc.decrypt(secret), "UTF-8"));
            assertEquals(content, new String(EccHelper.decrypt(secret, pri), "UTF-8"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEccSignAndVerify() {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("EC", "BC");
            keygen.initialize(192, SecureRandom.getInstance("SHA1PRNG"));
            KeyPair kp = keygen.generateKeyPair();
            byte[] pub = kp.getPublic().getEncoded();
            byte[] pri = kp.getPrivate().getEncoded();

            EccHelper ecc = new EccHelper(pub, pri);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append(String.valueOf(i));
            }
            byte[] data = sb.toString().getBytes(ConstField.UTF8);
            assertTrue(ecc.verify(ecc.sign(data), data));
            assertTrue(EccHelper.verify(ecc.sign(data), data, pub));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
