package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-4-25.
 */
@Description("长整形数组返回值")
public final class LongArrayResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("长整形数组返回值")
    public long[] value;

    public static LongArrayResp convert(long[] ls) {
        LongArrayResp la = new LongArrayResp();
        la.value = ls;
        return la;
    }
}
