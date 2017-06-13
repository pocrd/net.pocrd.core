package net.pocrd.core.test.model;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;

public class ORMObject {
    public long       l1;
    public BigDecimal bd1;
    public Blob       bl1;
    public boolean    b1;
    public byte       by1;
    public byte[]     bs1;
    public Clob       cl1;
    public double     d1;
    public float      f1;
    public int        i1;
    public String     s1;
    public short      sh1;

    private long       l;
    private BigDecimal bd;
    private Blob       bl;
    private boolean    b;
    private byte       by;
    private byte[]     bs;
    private Clob       cl;
    private double     d;
    private float      f;
    private int        i;
    private String     s;
    private short      sh;

    public long getL() {
        return l;
    }

    public void setL(long deviceId) {
        this.l = deviceId;
    }

    public BigDecimal getBd() {
        return bd;
    }

    public void setBd(BigDecimal bd) {
        this.bd = bd;
    }

    public Blob getBl() {
        return bl;
    }

    public void setBl(Blob bl) {
        this.bl = bl;
    }

    public boolean getB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public byte getBy() {
        return by;
    }

    public void setBy(byte by) {
        this.by = by;
    }

    public byte[] getBs() {
        return bs;
    }

    public void setBs(byte[] bs) {
        this.bs = bs;
    }

    public Clob getCl() {
        return cl;
    }

    public void setCl(Clob cl) {
        this.cl = cl;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public short getSh() {
        return sh;
    }

    public void setSh(short sh) {
        this.sh = sh;
    }
}
