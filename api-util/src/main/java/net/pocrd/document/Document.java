package net.pocrd.document;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 14-5-2.
 */
@Description("接口文档")
public class Document implements Serializable {
    @Description("应用接口信息")
    public List<MethodInfo>          apiList;
    @Description("通用异常信息")
    public List<CodeInfo>            codeList;
    @Description("通用返回值结构描述")
    public List<TypeStruct>          respStructList;
    @Description("系统级参数列表描述")
    public List<SystemParameterInfo> systemParameterInfoList;
}
