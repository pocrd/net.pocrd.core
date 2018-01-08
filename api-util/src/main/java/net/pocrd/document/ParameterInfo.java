package net.pocrd.document;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-5-2.
 */
@Description("参数信息")
public class ParameterInfo implements Serializable {
    @Description("参数类型")
    public String  type;
    @Description("默认值(非必选参数)")
    public String  defaultValue;
    @Description("验证规则(正则表达式)")
    public String  verifyRegex;
    @Description("验证失败提示")
    public String  verifyMsg;
    @Description("是否必选参数")
    public boolean isRequired;
    @Description("只能通过服务端注入来传入该参数")
    public boolean injectOnly;
    @Description("参数名")
    public String  name;
    @Description("参数描述")
    public String  description;
    @Description("该参数可以使用的隐式参数名")
    public String  serviceInjection;
    @Description("是否是集合或数组类型")
    public boolean isList;
    @Description("是否需要rsa加密")
    public boolean isRsaEncrypt;
    @Description("参数在接口中的次序, 当前可能的取值有 int0, int1...int9 str0, str1...str9")
    public String  sequence;
}
