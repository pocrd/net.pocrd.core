package net.pocrd.document;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 14-5-2.
 */
@Description("类型结构描述")
public class TypeStruct implements Serializable {
    @Description("结构名")
    public String          name;
    @Description("分组名")
    public String          groupName;
    @Description("成员")
    public List<FieldInfo> fieldList;
}
