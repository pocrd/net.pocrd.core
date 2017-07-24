package net.pocrd.entity;

import net.pocrd.define.ApiOpenState;
import net.pocrd.define.ResponseWrapper;
import net.pocrd.define.SecurityType;
import net.pocrd.define.Serializer;
import net.pocrd.util.SparseIntArray;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 接口信息
 */
public class ApiMethodInfo {
    /**
     * 未知资源
     */
    public static final ApiMethodInfo UnknownMethod = new ApiMethodInfo();

    static {
        UnknownMethod.methodName = "Unknown";
        UnknownMethod.description = "未知资源";
        UnknownMethod.errorCodes = new AbstractReturnCode[] {};
        UnknownMethod.parameterInfos = null;
        UnknownMethod.proxyMethodInfo = null;
        UnknownMethod.securityLevel = SecurityType.None;
    }

    /**
     * 返回值类型
     */
    public Class<?> returnType;

    /**
     * 接口是否被mock
     */
    public boolean mocked;

    /**
     * 静态声明的返回值 mock 值
     */
    public Object staticMockValue;

    /**
     * 返回类型为Collection时,actuallyGenericReturnType有值,值为实际的泛型信息
     */
    public Class<?> actuallyGenericReturnType;

    /**
     * 返回值类型对应的序列化工具
     */
    public Serializer<?> serializer;

    /**
     * 对返回的基本类型及其数组进行封装的封装器
     */
    public ResponseWrapper wrapper;

    /**
     * 方法名称
     */
    public String methodName;

    /**
     * 方法调用说明
     */
    public String description;

    /**
     * 方法详细说明
     */
    public String detail;

    /**
     * 方法需要的安全级别
     */
    public SecurityType securityLevel = SecurityType.None;

    /**
     * 可以访问该方法的权限集合
     */
    public Set<String> roleSet;

    /**
     * 资源所属组名
     */
    public String groupName;

    /**
     * 方法状态
     */
    public ApiOpenState state = ApiOpenState.CLOSED;

    /**
     * 结构化入参的默认值需要在生成的bytecode的常量中声明(需要常量声明的情况:入参为自定义的结构化入参,并且入参为非必填参数)
     */
    public boolean needDefaultValueConstDefined;

    /**
     * 参数类型
     */
    public ApiParameterInfo[] parameterInfos;

    /**
     * 该方法可能抛出的业务异常的errorcode集合
     */
    public AbstractReturnCode[] errorCodes;

    /**
     * 该方法声明的内部业务异常映射
     */
    public SparseIntArray innerCodeMap;

    /**
     * 该方法可能抛出的业务异常的errorcode int集合, 用于二分查找
     */
    public int[] errors;

    /**
     * 所代理的方法的信息
     */
    public Method proxyMethodInfo;

    /**
     * 被代理的方法所属的接口,dubbo interface
     */
    public Class<?> dubboInterface;

    /**
     * 提供被代理方法的实例
     */
    public Object serviceInstance;

    /**
     * 资源负责人
     */
    public String owner;

    /**
     * 资源组负责人
     */
    public String groupOwner;

    /**
     * 本接口是否只接受加密传输
     */
    public boolean encryptionOnly;

    /**
     * Integrated级别接口是否需要网关进行签名验证
     */
    public boolean needVerfiy;
}