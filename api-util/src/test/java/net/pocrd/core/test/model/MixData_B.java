package net.pocrd.core.test.model;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 2018/6/8.
 */
@Description("B")
public class MixData_B implements Serializable {
    @Description("b1")
    public int             b1;
    @Description("b2")
    public long            b2;
    @Description("b3")
    public byte            b3;
    @Description("b4")
    public String          b4;
    @Description("b5")
    public boolean         b5;
    @Description("b6")
    public MixData_B       b6;
    @Description("b7")
    public List<MixData_B> b7;

    public MixData_B() {
        b1 = (int)(Math.random() * Integer.MAX_VALUE);
        b2 = (long)(Math.random() * Long.MAX_VALUE);
        b3 = (byte)(Math.abs(Math.random()) * Byte.MAX_VALUE);
        b4 = "hello world";
        b5 = true;
    }
}
