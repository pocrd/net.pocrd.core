package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-4-25.
 */
@Description("长整形返回值")
public final class LongResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("长整形返回值")
    public long value;

    public static LongResp convert(long l) {
        LongResp lr = new LongResp();
        lr.value = l;
        return lr;
    }
}
