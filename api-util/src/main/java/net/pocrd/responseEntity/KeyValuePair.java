package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

@Description("键值对")
public final class KeyValuePair implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("键")
    public String key;
    @Description("值")
    public String value;

    public KeyValuePair() {
    }

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
