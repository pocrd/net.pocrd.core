package net.pocrd.document;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 14-5-2.
 * modified by sunji on 2014-8-12.
 */
@Description("编码信息")
public class CodeInfo implements Serializable {
    @Description("编码值")
    public int     code;
    @Description("编码名称")
    public String  name;
    @Description("编码描述")
    public String  desc;
    @Description("编码所属服务")
    public String  service;
    @Description("是否显示给客户端")
    public boolean isDesign;
}
