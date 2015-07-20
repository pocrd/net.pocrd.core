package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-5-2.
 */
@Description("返回原始string对象，不进行对象序列化")
public class RawString implements Serializable {
    @Description("要返回的字符串")
    public String value;
}
