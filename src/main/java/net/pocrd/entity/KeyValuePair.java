package net.pocrd.entity;

public class KeyValuePair {
    public static KeyValuePair[] empty = new KeyValuePair[0];
    public String key;
    public String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
