package net.pocrd.util;

public class HexStringUtil {
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static final String toHexString(byte[] bs) {
        if (bs == null) return null;
        char[] hexChars = new char[bs.length * 2];
        for (int i = 0; i < bs.length; i++) {
            int v = bs[i] & 0xff;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0f];
        }
        return new String(hexChars);
    }

    public static final byte[] toByteArray(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) return null;
        hexString = hexString.toLowerCase();
        char[] cs = hexString.toCharArray();
        byte[] bs = new byte[cs.length / 2];
        for (int i = 0; i < bs.length; i++) {
            char b1 = cs[i * 2];
            char b2 = cs[i * 2 + 1];
            bs[i] = (byte)(((b1 >= 'a' ? b1 - 'a' + 10 : b1 - '0') << 4) | (b2 >= 'a' ? b2 - 'a' + 10 : b2 - '0'));
        }
        return bs;
    }
}
