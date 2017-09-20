package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 14-5-2.
 */
@Description("接口返回值状态节点")
public class Response implements Serializable {
    @Description("当前服务端时间")
    public long systime;

    @Description("调用返回值")
    public int code;

    @Description("调用标识符")
    public String cid;

    @Description("API调用状态，code的信息请参考ApiCode定义文件")
    public List<CallState> stateList;

    @Description("服务端返回的通知事件集合")
    public List<KeyValuePair> notificationList;
}
