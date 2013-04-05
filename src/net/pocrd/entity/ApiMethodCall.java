package net.pocrd.entity;


public class ApiMethodCall {
    public static ApiMethodCall UnknownMethodCall;

    static {
        UnknownMethodCall = new ApiMethodCall();
        UnknownMethodCall.method = ApiMethodInfo.UnknownMethod;
        UnknownMethodCall.originCode = -1;
        UnknownMethodCall.returnCode = -1;
    }

    private ApiMethodCall() {
        returnCode = Integer.MIN_VALUE;
    }

    public ApiMethodCall(ApiMethodInfo method) {
        this();
        this.method = method;
    }

    /**
     * 接口信息
     */
    public ApiMethodInfo method;

    /**
     * 调用结果(序列化前)
     */
    public Object        result;

    /**
     * 执行中的额外消息
     */
    public StringBuilder Message    = new StringBuilder();

    /**
     * 调用耗时
     */
    public int           costTime;

    /**
     * 调用结果(序列化后)
     */
    public byte[]        resultBytes;

    /**
     * 返回值代码
     */
    private int      returnCode = 0;

    /**
     * 原始返回值代码，用于记录业务函数的原始返回代码
     */
    private int      originCode = 0;

    /**
     * 返回消息
     */
    public String        returnMessage;

    /**
     * 二进制数据起始位置
     */
    public int           byteStart;

    /**
     * 调用的具体参数
     */
    public String[]      parameters;

    public void setReturnCode(int code) {
        if (returnCode == Integer.MIN_VALUE) {
            returnCode = code;
            originCode = code;
        }
    }

    public int getReturnCode() {
        return returnCode;
    }

    void replaceReturnCode(int code) {
        originCode = returnCode;
        returnCode = code;
    }

    public int getOriginCode() {
        return originCode;
    }
}