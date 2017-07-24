package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-4-28.
 */
@Description("布尔类型数组返回值")
public final class BoolArrayResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("布尔类型数组返回值")
    public boolean[] value;

    public static BoolArrayResp convert(boolean[] bs) {
        BoolArrayResp ba = new BoolArrayResp();
        ba.value = bs;
        return ba;
    }
}
