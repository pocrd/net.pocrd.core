package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-4-28.
 */
@Description("布尔类型返回值")
public final class BoolResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("布尔类型返回值")
    public boolean value;

    public static BoolResp convert(boolean b) {
        BoolResp br = new BoolResp();
        br.value = b;
        return br;
    }
}
