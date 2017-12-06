package net.pocrd.define;

import java.nio.charset.Charset;

public class ConstField {
    public static final Charset UTF8                        = Charset.forName("utf-8");
    public static final Charset ASCII                       = Charset.forName("ascii");
    public static final String  coreEntityPackage           = "net.pocrd.responseEntity.";
    public static final byte[]  XML_START                   = "<xml>".getBytes(UTF8);
    public static final byte[]  XML_END                     = "</xml>".getBytes(UTF8);
    public static final byte[]  JSON_START                  = "{\"stat\":".getBytes(UTF8);
    public static final byte[]  JSON_CONTENT                = ",\"content\":[".getBytes(UTF8);
    public static final byte[]  XML_EMPTY                   = "<empty/>".getBytes(ConstField.UTF8);
    public static final byte[]  JSON_SPLIT                  = ",".getBytes(UTF8);
    public static final byte[]  JSON_END                    = "]}".getBytes(UTF8);
    public static final byte[]  JSON_EMPTY                  = "{}".getBytes(UTF8);
    public static final byte[]  JSONP_START                 = "(".getBytes(UTF8);
    public static final byte[]  JSONP_END                   = ");".getBytes(UTF8);
    public static final String  SET_COOKIE_TOKEN            = "net.pocrd.SET_COOKIE_TOKEN";
    public static final String  SET_COOKIE_STOKEN           = "net.pocrd.SET_COOKIE_STOKEN";
    public static final String  SET_COOKIE_USER_INFO        = "net.pocrd.SET_COOKIE_USER_INFO";
    public static final String  REDIRECT_TO                 = "net.pocrd.REDIRECT_TO";
    public static final String  CREDIT                      = "net.pocrd.CREDIT";
    public static final String  MSG                         = "net.pocrd.MSG";
    public static final String  SERVICE_LOG                 = "net.pocrd.SERVICE_LOG";
    public static final String  SERVICE_PARAM_EXPORT_PREFIX = "net.pocrd.SERVICE_PARAM_EXPORT_";
}
