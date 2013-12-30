package net.pocrd.define;

import java.nio.charset.Charset;

public class ConstField {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Charset  UTF8               = Charset.forName("utf-8");
    public static final byte[]   XML_START          = "<xml>".getBytes(UTF8);
    public static final byte[]   XML_END            = "</xml>".getBytes(UTF8);
    public static final byte[]   JSON_START         = "{\"stat\":".getBytes(UTF8);
    public static final byte[]   JSON_CONTENT       = ",\"content\":[".getBytes(UTF8);
    public static final byte[]   JSON_SPLIT         = ",".getBytes(UTF8);
    public static final byte[]   JSON_END           = "]}".getBytes(UTF8);
    public static final byte[]   JSON_EMPTY         = "{}".getBytes(UTF8);

}
