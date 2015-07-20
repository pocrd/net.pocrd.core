package net.pocrd.util;

import net.pocrd.annotation.ThreadSafe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@ThreadSafe
public class SHAUtil {
    public static byte[] computeSHA1(byte[] content) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            return sha1.digest(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String computeSHA1ToHex(byte[] content) {
        return HexStringUtil.toHexString(computeSHA1(content));
    }

    public static final String computeSHA1ToBase64(byte[] content) {
        return Base64Util.encodeToString(computeSHA1(content));
    }
}
