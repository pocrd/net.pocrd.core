package net.pocrd.entity;

public class ApiMethodCall {
    private static ApiMethodCall UnknownMethodCall;

    static {
        UnknownMethodCall = new ApiMethodCall();
        UnknownMethodCall.method = ApiMethodInfo.UnknownMethod;
        UnknownMethodCall.originCode = ApiReturnCode.UNKNOWN_METHOD.getCode();
        UnknownMethodCall.returnCode = ApiReturnCode.UNKNOWN_METHOD.getCode();
    }

    private ApiMethodCall() {}

    public ApiMethodCall(ApiMethodInfo method) {
        returnCode = ApiReturnCode.NO_ASSIGN.getCode();
        originCode = ApiReturnCode.NO_ASSIGN.getCode();
        this.method = method;
    }

    /**
     * 接口信息
     */
    public ApiMethodInfo method;

    /**
     * 调用结果(序列化前)
     */
    public Object result;

    /**
     * 返回值长度(未压缩前的byte数组长度)
     */
    public int resultLen;

    /**
     * 执行中的额外消息
     */
    public StringBuilder message = new StringBuilder();

    /**
     * 调用开始时间
     */
    public long startTime;

    /**
     * 调用耗时
     */
    public int costTime;

    /**
     * 返回值代码
     */
    private int returnCode = ApiReturnCode.SUCCESS.getCode();

    /**
     * 原始返回值代码，用于记录业务函数的原始返回代码
     */
    private int originCode = ApiReturnCode.SUCCESS.getCode();

    /**
     * 返回消息
     */
    private String returnMessage;

    /**
     * 二进制数据起始位置
     */
    public int byteStart;

    /**
     * dubbo 服务返回需要api进行记录的日志信息
     */
    public String serviceLog;

    /**
     * 调用的具体参数
     */
    public String[] parameters;

    public void setReturnCode(AbstractReturnCode code) {
        if (returnCode == ApiReturnCode.NO_ASSIGN.getCode()) {
            returnCode = code.getDisplay().getCode();
            originCode = code.getCode();
            returnMessage = code.getDisplay().getDesc();
        }
    }

    public void setReturnCode(int code, int displayCode, String message){
        if (returnCode == ApiReturnCode.NO_ASSIGN.getCode()) {
            returnCode = displayCode;
            originCode = code;
            returnMessage = message;
        }
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void replaceReturnCode(AbstractReturnCode code) {
        returnCode = code.getCode();
        returnMessage = code.getDesc();
    }

    public int getOriginCode() {
        return originCode;
    }
}