package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-5-10.
 */
@Description("键整形值对")
public final class KeyNumberPair implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("键")
    public String key;
    @Description("整形值")
    public int    value;

    public KeyNumberPair() {
    }

    public KeyNumberPair(String key, int value) {
        this.key = key;
        this.value = value;
    }
}