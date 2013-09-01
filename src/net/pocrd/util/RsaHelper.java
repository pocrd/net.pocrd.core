package net.pocrd.util;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

/**
 * RSA工具类, blockSize = keySize - 11;
 */
public class RsaHelper {
    private static final Logger  logger          = LogManager.getLogger("net.pocrd.util");

    private RSAPublicKeyImpl     publicKey;
    private RSAPrivateCrtKeyImpl privateKey;

    public RsaHelper(byte[] publicKey, byte[] privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            if (publicKey != null && publicKey.length > 0) {
                this.publicKey = (RSAPublicKeyImpl)keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            }
            if (privateKey != null && privateKey.length > 0) {
                this.privateKey = (RSAPrivateCrtKeyImpl)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
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
            int size = publicKey.getModulus().bitLength() / 8 - 11;
            ByteArrayOutputStream baos = new ByteArrayOutputStream((content.length + size - 1) / size * (size + 11));
            int left = 0;
            for (int i = 0; i < content.length;) {
                left = content.length - i;
                if (left > size) {
                    cipher.update(content, i, size);
                    i += size;
                } else {
                    cipher.update(content, i, left);
                    i += left;
                }
                baos.write(cipher.doFinal());
            }

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[] secret) {
        if (privateKey == null) {
            throw new RuntimeException("private key is null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int size = privateKey.getModulus().bitLength() / 8;
            ByteArrayOutputStream baos = new ByteArrayOutputStream((secret.length + size - 12) / (size - 11) * size);
            int left = 0;
            for (int i = 0; i < secret.length;) {
                left = secret.length - i;
                if (left > size) {
                    cipher.update(secret, i, size);
                    i += size;
                } else {
                    cipher.update(secret, i, left);
                    i += left;
                }

                baos.write(cipher.doFinal());
            }

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("rsa decrypt failed.", e);
        }
        return null;
    }

    public byte[] sign(byte[] content) {
        if (privateKey == null) {
            throw new RuntimeException("private key is null.");
        }
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
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
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(sign);
        } catch (Exception e) {
            logger.error("rsa verify failed.", e);
        }
        return false;
    }

    public static byte[] encrypt(byte[] content, byte[] publicKey) {
        return new RsaHelper(publicKey, null).encrypt(content);
    }

    public static byte[] decrypt(byte[] secret, byte[] privateKey) {
        return new RsaHelper(null, privateKey).decrypt(secret);
    }

    public static byte[] sign(byte[] content, byte[] privateKey) {
        return new RsaHelper(null, privateKey).sign(content);
    }

    public static boolean verify(byte[] sign, byte[] content, byte[] publicKey) {
        return new RsaHelper(publicKey, null).verify(sign, content);
    }
}
