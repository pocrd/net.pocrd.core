package net.pocrd.document;

import net.pocrd.annotation.Description;

import java.io.Serializable;

@Description("系统级参数")
public class SystemParameterInfo implements Serializable {
    @Description("参数名称")
    public String name;
    @Description("描述")
    public String desc;
}
