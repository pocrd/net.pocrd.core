package net.pocrd.entity;

import net.pocrd.define.ParamCreator;

public class ApiParameterInfo {
    /**
     * 参数类型
     */
    public Class<?> type;

    /**
     * 用于定义String类型参数取值的枚举类型
     */
    public Class<? extends Enum> verifyEnumType;

    /**
     * 当入参为Collection 或 数组时, 若actuallyGenericType有值, 则值为实际的泛型信息
     */
    public Class<?> actuallyGenericType;

    /**
     * 默认值字符串形式
     */
    public String defaultValue;

    /**
     * 验证字符串表达式
     */
    public String verifyRegex;

    /**
     * 验证错误提示
     */
    public String verifyMsg;

    /**
     * 是否必须
     */
    public boolean isRequired;

    /**
     * 是否加密传输, 用于客户端向服务端发送隐私信息
     */
    public boolean isRsaEncrypted;

    /**
     * 参数名
     */
    public String name;

    /**
     * 名字列表
     */
    public String[] names;

    /**
     * 参数描述
     */
    public String description;

    /**
     * 是否是自动注入参数
     */
    public boolean isAutowired;

    /**
     * 参数的默认值是否需要在常量中定义
     */
    public boolean needDefaultValueConstDefined;

    /**
     * 由于安全原因需要在日志系统中忽略的参数
     */
    public boolean ignoreForSecurity;

    /**
     * 该参数在接口中的次序, 与类型相关. 当前可能的取值有 int0, int1...int9 str0, str1...str9
     * 目前被用在etl处理接口调用日志时按照该顺序放置各个参数
     */
    public String sequence;

    /**
     * 参数发生器
     * 详细见 DesignedParameter 的描述
     */
    public ParamCreator creator;
}
