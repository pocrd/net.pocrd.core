package net.pocrd.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import net.pocrd.define.ConstField;

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

    public RsaHelper(String publicKey, String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            if (publicKey != null && publicKey.length() > 0) {
                byte[] encodedKey = Base64Util.decode(publicKey);
                this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            }
            if (privateKey != null && privateKey.length() > 0) {
                byte[] encodedKey = Base64Util.decode(privateKey);
                this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public String encrypt(String content) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] plainBytes = content.getBytes(ConstField.UTF8);
            byte[] output = cipher.doFinal(plainBytes);

            return Base64Util.encodeToString(output);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public String decrypt(String secret) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            byte[] secretBytes = secret.getBytes(ConstField.UTF8);
            byte[] output = cipher.doFinal(secretBytes);

            return Base64Util.encodeToString(output);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }  
    
    public String sign(String content) {
        if (privateKey == null) {
            throw new RuntimeException("private key is null.");
        }
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(content.getBytes(ConstField.UTF8));
            byte[] signed = signature.sign();
            return Base64Util.encodeToString(signed);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public boolean verify(String sign, String content) {
        if (publicKey == null) {
            throw new RuntimeException("public key is null.");
        }
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(publicKey);
            signature.update(content.getBytes(ConstField.UTF8));

            return signature.verify(Base64Util.decode(sign));
        } catch (Exception e) {
            logger.error(e);
        }
        return false;
    }
    
    public static String encrypt(String content, String publicKey){
        return new RsaHelper(publicKey, null).encrypt(content);
    }
    
    public static String decrypt(String secret, String privateKey){
        return new RsaHelper(null, privateKey).decrypt(secret);
    }

    public static String sign(String content, String privateKey) {
        return new RsaHelper(null, privateKey).sign(content);
    }

    public static boolean verify(String sign, String content, String publicKey) {
        return new RsaHelper(publicKey, null).verify(sign, content);
    }
}
