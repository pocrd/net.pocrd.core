package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * 要求网关返回原始string对象，不进行对象序列化
 * RawString不作为直接对外暴露的对象，外部系统应该不感知，请使用net.pocrd.util.RawString做为替代
 *
 * @see net.pocrd.util.RawString
 */
@Deprecated
@Description("字符串，请使用net.pocrd.util.RawString替换")
public class RawString implements Serializable {
    @Description("值")
    public String value;

    public RawString() {

    }

    public RawString(String str) {
        value = str;
    }
}
