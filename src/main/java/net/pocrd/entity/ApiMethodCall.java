package net.pocrd.entity;

public class ApiMethodCall {
    public static ApiMethodCall UnknownMethodCall;

    static {
        UnknownMethodCall = new ApiMethodCall();
        UnknownMethodCall.method = ApiMethodInfo.UnknownMethod;
        UnknownMethodCall.originCode = ReturnCode.UNKNOWN_METHOD;
        UnknownMethodCall.returnCode = ReturnCode.UNKNOWN_METHOD;
    }

    private ApiMethodCall() {}

    public ApiMethodCall(ApiMethodInfo method) {
        returnCode = ReturnCode.NO_ASSIGN;
        originCode = ReturnCode.NO_ASSIGN;
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
     * 返回值长度(未压缩前的byte数组长度)
     */
    public int           resultLen;

    /**
     * 执行中的额外消息
     */
    public StringBuilder message    = new StringBuilder();

    /**
     * 调用开始时间
     */
    public long          startTime;

    /**
     * 调用耗时
     */
    public int           costTime;

    /**
     * 返回值代码
     */
    private ReturnCode   returnCode = ReturnCode.SUCCESS;

    /**
     * 原始返回值代码，用于记录业务函数的原始返回代码
     */
    private ReturnCode   originCode = ReturnCode.SUCCESS;

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

    public void setReturnCode(ReturnCode code) {
        if (returnCode == ReturnCode.NO_ASSIGN) {
            returnCode = code;
            originCode = code;
        }
    }

    public boolean isNoAssign() {
        return returnCode == ReturnCode.NO_ASSIGN;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public void replaceReturnCode(ReturnCode code) {
        originCode = returnCode;
        returnCode = code;
    }

    public ReturnCode getOriginCode() {
        return originCode;
    }
}