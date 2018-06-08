package net.pocrd.dubboext;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import net.pocrd.define.ConstField;
import net.pocrd.define.ServiceInjectable;
import net.pocrd.responseEntity.CreditNotification;
import net.pocrd.responseEntity.MessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rendong on 14-5-15.
 */
public class DubboExtProperty {
    private static       Logger logger               = LoggerFactory.getLogger(DubboExtProperty.class);
    private static final String JSONOBJECT_SEPARATOR = ",";
    public static final  String LOG_SPLITTER         = new String(new char[] { ' ', 2 });

    /**
     * 覆盖写入token信息 以及 stoken信息 和 stoken的cookie过期时间
     *
     * @param token
     * @param stoken
     * @param stkDuration
     */
    public static void setCookieToken(String token, String stoken, int stkDuration) {
        RpcContext context = RpcContext.getContext();
        if (token == null || stoken == null) {
            throw new RuntimeException("token or stoken is null.");
        }
        context.setNotification(ConstField.SET_COOKIE_TOKEN, token);
        context.setNotification(ConstField.SET_COOKIE_STOKEN, stoken + "|" + stkDuration);
    }

    /**
     * 覆盖写入用户信息
     *
     * @param info
     */
    public static void setCookieUserInfo(String info) {
        if (info != null) {
            RpcContext.getContext().setNotification(ConstField.SET_COOKIE_USER_INFO, info);
        }
    }

    /**
     * 覆盖写入重定向URL
     *
     * @param url
     */
    public static void setRedirectUrl(String url) {
        if (url != null) {
            RpcContext.getContext().setNotification(ConstField.REDIRECT_TO, url);
        }
    }

    /**
     * 累加写入积分获取信息
     *
     * @param creditInfo
     */
    public static void addCreditsGain(CreditNotification creditInfo) {
        RpcContext context = RpcContext.getContext();
        if (creditInfo != null) {
            String tmp = context.getNotification(ConstField.CREDIT);
            String info = tmp == null ? JSON.toJSONString(creditInfo) : tmp + JSONOBJECT_SEPARATOR + JSON.toJSONString(creditInfo);
            if (info.length() < 1024) {
                context.setNotification(ConstField.CREDIT, info);
            } else {
                logger.error("Notification CREDIT is too long. " + info);
            }
        }
    }

    /**
     * 累加
     *
     * @param msgInfo
     */
    public static void addMessageInfo(MessageNotification msgInfo) {
        RpcContext context = RpcContext.getContext();
        if (msgInfo != null) {
            String tmp = context.getNotification(ConstField.MSG);
            String info = tmp == null ? JSON.toJSONString(msgInfo) : tmp + JSONOBJECT_SEPARATOR + JSON.toJSONString(msgInfo);
            if (info.length() < 1024) {
                context.setNotification(ConstField.MSG, info);
            } else {
                logger.error("Notification MSG is too long. " + info);
            }
        }
    }

    /**
     * 上报一些核心日志信息至api网关, 每个微服务记录的日志长度不超过500字符
     *
     * @param log
     */
    public static void appendServiceLog(String log) {
        RpcContext context = RpcContext.getContext();
        if (log != null) {
            String tmp = context.getNotification(ConstField.SERVICE_LOG);
            String info = tmp == null ? log : tmp + LOG_SPLITTER + log;
            if (info.length() < 1024) {
                context.setNotification(ConstField.SERVICE_LOG, info);
            } else {
                context.setNotification(ConstField.SERVICE_LOG, info.substring(0, 1024));
                logger.error("Notification SERVICE_LOG is too long. " + log);
            }
        }
    }

    /**
     * 对外暴露额外的可注入参数
     */
    public static void exportServiceData(ServiceInjectable.InjectionData data) {
        RpcContext context = RpcContext.getContext();
        if (data != null) {
            String name = ConstField.SERVICE_PARAM_EXPORT_PREFIX + data.getName();
            Map<String, String> notifications = context.getNotifications();
            if (notifications != null) {
                if (notifications.containsKey(name)) {
                    ServiceInjectable.InjectionData origin = JSON.parseObject(notifications.get(name), data.getClass());
                    origin.batchMerge(data);
                    context.setNotification(name, JSON.toJSONString(origin));
                    return;
                }
            }
            context.setNotification(name, JSON.toJSONString(data));
        }
    }
}
