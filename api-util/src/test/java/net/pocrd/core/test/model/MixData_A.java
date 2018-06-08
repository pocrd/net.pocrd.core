package net.pocrd.core.test.model;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 2018/6/8.
 */
@Description("A")
public class MixData_A implements Serializable {
    @Description("a1")
    public int             a1;
    @Description("a2")
    public long            a2;
    @Description("a3")
    public byte            a3;
    @Description("a4")
    public String          a4;
    @Description("a5")
    public boolean         a5;
    @Description("a6")
    public MixData_A       a6;
    @Description("a7")
    public List<MixData_A> a7;

    public MixData_A() {
        a1 = (int)(Math.random() * Integer.MAX_VALUE);
        a2 = (long)(Math.random() * Long.MAX_VALUE);
        a3 = (byte)(Math.abs(Math.random()) * Byte.MAX_VALUE);
        a4 = "hello world";
        a5 = true;
    }
}
