package net.pocrd.entity;

import net.pocrd.annotation.Description;
import net.pocrd.util.SparseArray;

/**
 * 基本错误码，由公共代码模块返回
 * 
 * @author rendong
 */
public class ReturnCode {
    private static SparseArray<ReturnCode> map                   = new SparseArray<ReturnCode>(100);

    @Description("未分配返回值")
    public final static ReturnCode         NO_ASSIGN             = new ReturnCode(Integer.MIN_VALUE);

    @Description("服务端返回未知错误")
    public final static ReturnCode         UNKNOWN_ERROR         = new ReturnCode(-1);

    @Description("method参数服务端无法识别")
    public final static ReturnCode         UNKNOWN_METHOD        = new ReturnCode(-2);

    /**
     * 内部服务异常, 对外显示为UNKNOWN_ERROR
     */
    public final static ReturnCode         INTERNAL_SERVER_ERROR = new ReturnCode(-3, UNKNOWN_ERROR);

    @Description("参数错误")
    public final static ReturnCode         PARAMETER_ERROR       = new ReturnCode(-4);

    @Description("成功")
    public final static ReturnCode         SUCCESS               = new ReturnCode(0);

    private int                            code;
    private ReturnCode                     shadow;

    public static ReturnCode findCode(int c) {
        return map.get(c);
    }

    protected ReturnCode(int code) {
        this.code = code;
        if (map.get(code) != null) {
            throw new RuntimeException("ambiguous code definition. " + code);
        }
        map.put(code, this);
    }

    protected ReturnCode(int code, ReturnCode shadow) {
        this.code = code;
        this.shadow = shadow;
        if (map.get(code) != null) {
            throw new RuntimeException("ambiguous code definition. " + code);
        }
        map.put(code, this);
    }

    public int getCode() {
        return code;
    }

    public ReturnCode getShadow() {
        return shadow;
    }
}
