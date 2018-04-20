package net.pocrd.document;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 14-5-2.
 */
@Description("接口信息")
public class MethodInfo implements Serializable {
    @Description("返回值类型")
    public String              returnType;
    @Description("接口名")
    public String              methodName;
    @Description("接口简介")
    public String              description;
    @Description("接口详细信息")
    public String              detail;
    @Description("调用接口所需安全级别")
    public String              securityLevel;
    @Description("接口分组名")
    public String              groupName;
    @Description("接口所属子系统id")
    public int                 subSystemId;
    @Description("接口可用状态")
    public String              state;
    @Description("接口返回值类型结构描述")
    public List<TypeStruct>    respStructList;
    @Description("接口参数列表信息")
    public List<ParameterInfo> parameterInfoList;
    @Description("接口返回值类型结构描述")
    public List<TypeStruct>    reqStructList;
    @Description("接口返回业务异常列表")
    public List<CodeInfo>      errorCodeList;
    @Description("该方法会返回的隐式参数列表")
    public List<String>        exportParams;
    @Description("接口组负责人")
    public String              groupOwner;
    @Description("接口负责人")
    public String              methodOwner;
    @Description("是否只允许通过加密通道访问")
    public boolean             encryptionOnly;
    @Description("Integared级别接口是否需要网关对请求进行签名验证")
    public boolean             needVerify;
}
