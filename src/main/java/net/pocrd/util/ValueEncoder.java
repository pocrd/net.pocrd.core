package net.pocrd.util;

import net.pocrd.define.ConstField;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by rendong on 14-5-1.
 */
public class ValueEncoder {
    private static final byte[][] bs;
    private static final byte[][] byteValue1 = new byte[100][];
    private static final byte[][] byteValue2 = new byte[1000][];

    static {
        String[] ss = new String[]{"false", "true", "-", String.valueOf(Integer.MIN_VALUE)};
        bs = new byte[ss.length][];
        for (int i = 0; i < ss.length; i++) {
            bs[i] = ss[i].getBytes(ConstField.UTF8);
        }
        byte b0 = "0".getBytes(ConstField.UTF8)[0];
        for (int i = 0; i < 1000; i++) {
            if (i < 10) {
                byteValue1[i] = ("" + i).getBytes(ConstField.UTF8);
                byteValue2[i] = new byte[3];
                byteValue2[i][0] = b0;
                byteValue2[i][1] = b0;
                byteValue2[i][2] = byteValue1[i][0];
            } else if (i < 100) {
                byteValue1[i] = ("" + i).getBytes(ConstField.UTF8);
                byteValue2[i] = new byte[3];
                byteValue2[i][0] = b0;
                byteValue2[i][1] = byteValue1[i][0];
                byteValue2[i][2] = byteValue1[i][1];
            } else {
                byteValue2[i] = ("" + i).getBytes(ConstField.UTF8);
            }
        }
    }

    public static void writeBytes(boolean b, OutputStream out) throws IOException {
        if (b) {
            out.write(bs[1]);
        } else {
            out.write(bs[0]);
        }
    }
    public static void writeBytes(byte b, OutputStream out) throws IOException {
        int i = b;
        if (i < 0) {
            out.write(bs[2]);
            i = -i;
        }
        out.write(byteValue1[i]);
    }
    public static void writeBytes(char c, OutputStream out) throws IOException {
        int i = c;
        if (i < 0) {
            out.write(bs[2]);
            i = -i;
        }
        if (i < 100) {
            out.write(byteValue1[i]);
        } else if (i < 1000) {
            out.write(byteValue2[i]);
        } else {
            int q, r;
            q = i / 1000;
            r = i - (q << 10) + (q << 4) + (q << 3);
            out.write(q < 100 ? byteValue1[q] : byteValue2[q]);
            out.write(byteValue2[r]);
        }
    }
    public static void writeBytes(short s, OutputStream out) throws IOException {
        int i = s;
        if (i < 0) {
            out.write(bs[2]);
            i = -i;
        }
        if (i < 100) {
            out.write(byteValue1[i]);
        } else if (i < 1000) {
            out.write(byteValue2[i]);
        } else {
            int q, r;
            q = i / 1000;
            r = i - (q << 10) + (q << 4) + (q << 3);
            out.write(q < 100 ? byteValue1[q] : byteValue2[q]);
            out.write(byteValue2[r]);
        }
    }
    public static void writeBytes(int i, OutputStream out) throws IOException {
        if (i == Integer.MIN_VALUE) {
            out.write(bs[3]);
            return;
        }
        if (i < 0) {
            out.write(bs[2]);
            i = -i;
        }
        if (i < 100) {
            out.write(byteValue1[i]);
        } else if (i < 1000) {
            out.write(byteValue2[i]);
        } else if (i < 1000000) {
            int q, r;
            q = i / 1000;
            r = i - (q << 10) + (q << 4) + (q << 3);
            out.write(q < 100 ? byteValue1[q] : byteValue2[q]);
            out.write(byteValue2[r]);
        } else {
            out.write((String.valueOf(i).getBytes(ConstField.UTF8)));
        }
    }
    public static void writeBytes(float f, OutputStream out) throws IOException {
        out.write((String.valueOf(f).getBytes(ConstField.UTF8)));
    }
    public static void writeBytes(long l, OutputStream out) throws IOException {
        out.write((String.valueOf(l).getBytes(ConstField.UTF8)));
    }
    public static void writeBytes(double d, OutputStream out) throws IOException {
        out.write((String.valueOf(d).getBytes(ConstField.UTF8)));
    }
}
