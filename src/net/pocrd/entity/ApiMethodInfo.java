package net.pocrd.entity;

import java.lang.reflect.Method;

import net.pocrd.define.ApiOpenState;
import net.pocrd.define.SecurityType;

/** 
 
 
*/
public class ApiMethodInfo {
    /**
     * 未知资源
     */
    public static final ApiMethodInfo UnknownMethod = new ApiMethodInfo();

    static {
        UnknownMethod.methodName = "Unknown";
        UnknownMethod.description = "未知资源";
        UnknownMethod.errorCodes = new int[] {};
        UnknownMethod.parameterInfos = null;
        UnknownMethod.proxyMethodInfo = null;
        UnknownMethod.securityLevel = SecurityType.None;
    }

    /**
     * 返回值类型
     */
    public Class<?>                   returnType;

    /**
     * 方法名称
     */
    public String                     methodName;

    /**
     * 方法调用说明
     */
    public String                     description;

    /**
     * 返回值类型string表示
     */
    public String                     returnTypeString;

    /**
     * 方法需要的安全级别
     */
    public SecurityType               securityLevel = SecurityType.None;

    /**
     * 资源所属组名
     */
    public String                     groupName;

    /**
     * 方法状态
     */
    public ApiOpenState               state         = ApiOpenState.OPEN;

    /**
     * 参数类型
     */
    public ApiParameterInfo[]         parameterInfos;

    /**
     * 该方法可能抛出的业务异常的errorcode集合
     */
    public int[]                      errorCodes;

    /**
     * 所代理的方法的信息
     */
    public Method                     proxyMethodInfo;
}