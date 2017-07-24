package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * @author guankaiqiang
 */
@Description("积分通知")
public final class CreditNotification implements Serializable {
    @Description("描述,为何送积分")
    public String description;
    @Description("积分值")
    public long   credit;
    @Description("提示,送了多少积分")
    public String notification;
}