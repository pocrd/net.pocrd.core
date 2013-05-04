package net.pocrd.entity;

import net.pocrd.annotation.Description;
import net.pocrd.util.SparseArray;

/**
 * 基本错误码，由公共代码模块返回
 * 
 * @author rendong
 */
public class ReturnCode {
    private static SparseArray<ReturnCode> map                            = new SparseArray<ReturnCode>(100);

    @Description("未分配返回值")
    public final static ReturnCode         NO_ASSIGN                      = new ReturnCode("NO_ASSIGN", Integer.MIN_VALUE);

    @Description("服务端返回未知错误")
    public final static ReturnCode         UNKNOWN_ERROR                  = new ReturnCode("UNKNOWN_ERROR", -1);

    @Description("method参数服务端无法识别")
    public final static ReturnCode         UNKNOWN_METHOD                 = new ReturnCode("UNKNOWN_METHOD", -2);

    /**
     * 内部服务异常, 对外显示为UNKNOWN_ERROR
     */
    public final static ReturnCode         INTERNAL_SERVER_ERROR          = new ReturnCode("INTERNAL_SERVER_ERROR", -3, UNKNOWN_ERROR);

    @Description("参数错误")
    public final static ReturnCode         PARAMETER_ERROR                = new ReturnCode("PARAMETER_ERROR", -4);

    @Description("访问被拒绝")
    public final static ReturnCode         ACCESS_DENIED                  = new ReturnCode("ACCESS_DENIED", -5);

    public final static ReturnCode         ACCESS_DENIED_MISSING_TOKEN    = new ReturnCode("ACCESS_DENIED_MISSING_TOKEN", -501, ACCESS_DENIED);

    public final static ReturnCode         ACCESS_DENIED_UNMATCH_SECURITY = new ReturnCode("ACCESS_DENIED_UNMATCH_SECURITY", -502, ACCESS_DENIED);

    @Description("签名错误")
    public final static ReturnCode         SIGNATURE_ERROR                = new ReturnCode("SIGNATURE_ERROR", -6);

    @Description("请求解析错误")
    public final static ReturnCode         REQUEST_PARSE_ERROR            = new ReturnCode("REQUEST_PARSE_ERROR", -7);

    public final static ReturnCode         SERIALIZE_FAILED               = new ReturnCode("SERIALIZE_FAILED", -8, UNKNOWN_ERROR);

    @Description("成功")
    public final static ReturnCode         SUCCESS                        = new ReturnCode("SUCCESS", 0);

    private String                         name;
    private int                            code;
    private ReturnCode                     shadow;

    public static ReturnCode findCode(int c) {
        return map.get(c);
    }

    protected ReturnCode(String name, int code) {
        this.name = name;
        this.code = code;
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
