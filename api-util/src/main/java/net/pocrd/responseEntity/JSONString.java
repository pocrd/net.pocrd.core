package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

@Description("返回json格式的string")
public final class JSONString implements Serializable {
    @Description("json string")
    public String value;
}