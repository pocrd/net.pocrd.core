package net.pocrd.util;

/**
 * Created by rendong on 14-5-2.
 */

import java.io.Serializable;

/**
 * 要求网关返回原始string对象，不进行对象序列化
 */
public class RawString implements Serializable{
    public String value;

    public RawString() {

    }

    public RawString(String str) {
        value = str;
    }
}
