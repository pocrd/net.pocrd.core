package net.pocrd.core.test.model;

/**
 * Created by gkq on 2014/5/21.
 */

import net.pocrd.annotation.Description;

@Description("身高体重数据")
public class KVData {
    @Description("用户id")
    public long   personId;
    @Description("设备id")
    public String deviceId;
    @Description("数据类型，0表示体重，1表示身高")
    public char   type;
    @Description("数据时间")
    public long   createdTime;
    @Description("数据值")
    public String value;
}
