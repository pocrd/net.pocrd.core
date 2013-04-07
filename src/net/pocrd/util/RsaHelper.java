package net.pocrd.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RSA工具类
 */
public class RsaHelper {
    public static final String  SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final Logger logger          = LogManager.getLogger("net.pocrd.util");

    private PublicKey           publicKey;
    private PrivateKey          privateKey;

    public RsaHelper(byte[] publicKey, byte[] privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            if (publicKey != null && publicKey.length > 0) {
                this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            }
            if (privateKey != null && privateKey.length > 0) {
                this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[] content) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[] secret) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            return cipher.doFinal(secret);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }  
    
    public byte[] sign(byte[] content) {
        if (privateKey == null) {
            throw new RuntimeException("private key is null.");
        }
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(content);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(byte[] sign, byte[] content) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(publicKey);
            signature.update(content);

            return signature.verify(sign);
        } catch (Exception e) {
            logger.error(e);
        }
        return false;
    }
    
    public static byte[] encrypt(byte[] content, byte[] publicKey){
        return new RsaHelper(publicKey, null).encrypt(content);
    }
    
    public static byte[] decrypt(byte[] secret, byte[] privateKey){
        return new RsaHelper(null, privateKey).decrypt(secret);
    }

    public static byte[] sign(byte[] content, byte[] privateKey) {
        return new RsaHelper(null, privateKey).sign(content);
    }

    public static boolean verify(byte[] sign, byte[] content, byte[] publicKey) {
        return new RsaHelper(publicKey, null).verify(sign, content);
    }
}
