package net.pocrd.entity;

import net.pocrd.annotation.Description;
import net.pocrd.util.SparseArray;

/**
 * 基本错误码，由公共代码模块返回
 * 
 * @author rendong
 */
public class ReturnCode {
    private static SparseArray<ReturnCode> map                              = new SparseArray<ReturnCode>(100);

    public final static int                C_NO_ASSIGN                      = Integer.MIN_VALUE;
    @Description("未分配返回值")
    public final static ReturnCode         NO_ASSIGN                        = new ReturnCode("NO_ASSIGN", C_NO_ASSIGN);

    public final static int                C_UNKNOWN_ERROR                  = -100;
    @Description("服务端返回未知错误")
    public final static ReturnCode         UNKNOWN_ERROR                    = new ReturnCode("UNKNOWN_ERROR", C_UNKNOWN_ERROR);

    /**
     * 内部服务异常, 对外显示为UNKNOWN_ERROR
     */
    public final static int                C_INTERNAL_SERVER_ERROR          = -101;
    public final static ReturnCode         INTERNAL_SERVER_ERROR            = new ReturnCode("INTERNAL_SERVER_ERROR", C_INTERNAL_SERVER_ERROR,
                                                                                    UNKNOWN_ERROR);

    /**
     * 内部序列化异常, 对外显示为UNKNOWN_ERROR
     */
    public final static int                C_SERIALIZE_FAILED               = -102;
    public final static ReturnCode         SERIALIZE_FAILED                 = new ReturnCode("SERIALIZE_FAILED", C_SERIALIZE_FAILED, UNKNOWN_ERROR);

    /**
     * ip受限, 对外显示为UNKNOWN_ERROR
     */
    public final static int                C_IP_DENIED                      = -103;
    public final static ReturnCode         IP_DENIED                        = new ReturnCode("IP_DENIED", C_IP_DENIED, UNKNOWN_ERROR);

    /**
     * 严重错误, 对外显示为UNKNOWN_ERROR
     */
    public final static int                C_FATAL_ERROR                    = -104;
    public final static ReturnCode         FATAL_ERROR                      = new ReturnCode("FATAL_ERROR", C_FATAL_ERROR, UNKNOWN_ERROR);

    public final static int                C_UNKNOWN_METHOD                 = -200;
    @Description("method参数服务端无法识别")
    public final static ReturnCode         UNKNOWN_METHOD                   = new ReturnCode("UNKNOWN_METHOD", C_UNKNOWN_METHOD);

    public final static int                C_PARAMETER_ERROR                = -300;
    @Description("参数错误")
    public final static ReturnCode         PARAMETER_ERROR                  = new ReturnCode("PARAMETER_ERROR", C_PARAMETER_ERROR);

    public final static int                C_ACCESS_DENIED                  = -400;
    @Description("访问被拒绝")
    public final static ReturnCode         ACCESS_DENIED                    = new ReturnCode("ACCESS_DENIED", C_ACCESS_DENIED);

    /**
     * 未提供token, 对外显示为ACCESS_DENIED
     */
    public final static int                C_ACCESS_DENIED_MISSING_TOKEN    = -401;
    public final static ReturnCode         ACCESS_DENIED_MISSING_TOKEN      = new ReturnCode("ACCESS_DENIED_MISSING_TOKEN",
                                                                                    C_ACCESS_DENIED_MISSING_TOKEN, ACCESS_DENIED);

    /**
     * 未达到接口所需安全级别, 对外显示为ACCESS_DENIED
     */
    public final static int                C_ACCESS_DENIED_UNMATCH_SECURITY = -402;
    public final static ReturnCode         ACCESS_DENIED_UNMATCH_SECURITY   = new ReturnCode("ACCESS_DENIED_UNMATCH_SECURITY",
                                                                                    C_ACCESS_DENIED_UNMATCH_SECURITY, ACCESS_DENIED);

    public final static int                C_SIGNATURE_ERROR                = -500;
    @Description("签名错误")
    public final static ReturnCode         SIGNATURE_ERROR                  = new ReturnCode("SIGNATURE_ERROR", C_SIGNATURE_ERROR);

    public final static int                C_REQUEST_PARSE_ERROR            = -600;
    @Description("请求解析错误")
    public final static ReturnCode         REQUEST_PARSE_ERROR              = new ReturnCode("REQUEST_PARSE_ERROR", C_REQUEST_PARSE_ERROR);

    public final static int                C_SUCCESS                        = 0;
    @Description("成功")
    public final static ReturnCode         SUCCESS                          = new ReturnCode("SUCCESS", C_SUCCESS);

    private final String                   name;
    private final int                      code;
    private final ReturnCode               shadow;

    public static ReturnCode findCode(int c) {
        return map.get(c);
    }

    protected ReturnCode(String name, int code) {
        this.name = name;
        this.code = code;
        this.shadow = null;
        if (map.get(code) != null) {
            throw new RuntimeException("ambiguous code definition. " + code);
        }
        map.put(code, this);
    }

    protected ReturnCode(String name, int code, ReturnCode shadow) {
        this.name = name;
        this.code = code;
        this.shadow = shadow;
        if (map.get(code) != null) {
            throw new RuntimeException("ambiguous code definition. " + code);
        }
        map.put(code, this);
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public ReturnCode getShadow() {
        return shadow;
    }
}
