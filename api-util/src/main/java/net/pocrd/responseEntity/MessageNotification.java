package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * @author guankaiqiang
 */
@Description("消息通知")
public final class MessageNotification implements Serializable {
    @Description("消息内容")
    public String content;
    @Description("消息Id")
    public String msgId;
    @Description("消息类型0: 系统消息,1:通知消息,2: 聊天消息,3:群消息,4:留言消息,5:普通聊天 控制消息")
    public int    type;
    @Description("消息内容类型")
    public int    subType;
    @Description("发送方Id")
    public long   fromUserId;
    @Description("接收方Id")
    public long   toUserId;
}