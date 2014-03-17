package net.pocrd.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAUtil {
    public static byte[] computeSHA1(byte[] content) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            return sha1.digest(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
