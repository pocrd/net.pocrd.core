package net.pocrd.dubboext;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import net.pocrd.define.ConstField;
import net.pocrd.responseEntity.CreditNotification;
import net.pocrd.responseEntity.MessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by rendong on 14-5-15.
 */
public class DubboExtProperty {
    private static       Logger logger               = LoggerFactory.getLogger(DubboExtProperty.class);
    private static final String JSONOBJECT_SEPARATOR = ",";
    public static final  String LOG_SPLITTER         = new String(new char[] { ' ', 2 });

    /**
     * 合并 ERROR_CODE_EXT, CREDIT, SERVICE_LOG 类型的notification
     *
     * @param rpcNotification
     */
    public static void mergeKnownNotifications(Map<String, String> rpcNotification) {
        for (Entry<String, String> entry : rpcNotification.entrySet()) {
            RpcContext context = RpcContext.getContext();
            Map<String, String> origin = context.getContext().getNotifications();
            if (ConstField.ERROR_CODE_EXT.equals(entry.getKey())) {
                //不使用provider的errorcode替换自身的
                String currentErrorCode = origin.get(entry.getKey());
                if (currentErrorCode != null && !currentErrorCode.isEmpty()) {
                    logger.info(
                            "provider return an error code, but current service has set an error code. current error code:{},return error code:{}",
                            currentErrorCode, entry.getValue());
                } else {
                    origin.put(entry.getKey(), entry.getValue());
                }
            } else if (ConstField.CREDIT.equals(entry.getKey()) || ConstField.MSG.equals(entry.getKey())) {
                //累加通知
                String tmp = origin.get(entry.getKey());
                if (tmp == null) {
                    origin.put(entry.getKey(), entry.getValue());
                } else {
                    origin.put(entry.getKey(), tmp + JSONOBJECT_SEPARATOR + entry.getValue());
                }
            } else if (ConstField.SERVICE_LOG.equals(entry.getKey())) {
                String tmp = origin.get(entry.getKey());
                if (tmp == null) {
                    origin.put(entry.getKey(), entry.getValue());
                } else {
                    origin.put(entry.getKey(), tmp + LOG_SPLITTER + entry.getValue());
                }
            }
        }

    }

    /**
     * 覆盖写入token信息 以及 stoken信息 和 stoken的cookie过期时间
     *
     * @param token
     * @param stoken
     * @param stkDuration
     */
    public static void setCookieToken(String token, String stoken, int stkDuration) {
        RpcContext context = RpcContext.getContext();
        if (token != null) {
            context.setNotification(ConstField.SET_COOKIE_TOKEN, token);
        }
        if (stoken != null) {
            context.setNotification(ConstField.SET_COOKIE_STOKEN, stoken + "|" + stkDuration);
        }
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
     * 覆盖
     *
     * @param url
     */
    public static void setRedirectUrl(String url) {
        if (url != null) {
            RpcContext.getContext().setNotification(ConstField.REDIRECT_TO, url);
        }
    }

    /**
     * 累加
     *
     * @param creditInfo
     */
    public static void addCreditsGain(CreditNotification creditInfo) {
        RpcContext context = RpcContext.getContext();
        if (creditInfo != null) {
            String tmp = context.getNotification(ConstField.CREDIT);
            if (tmp == null) {
                context.setNotification(ConstField.CREDIT, JSON.toJSONString(creditInfo));
            } else {
                context.setNotification(ConstField.CREDIT,
                        tmp + JSONOBJECT_SEPARATOR + JSON.toJSONString(creditInfo));
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
            if (tmp == null) {
                context.setNotification(ConstField.MSG, JSON.toJSONString(msgInfo));
            } else {
                context.setNotification(ConstField.MSG, tmp + JSONOBJECT_SEPARATOR + JSON.toJSONString(msgInfo));
            }
        }
    }

    public static String getMessageInfo() {
        return RpcContext.getContext().getNotification(ConstField.MSG);
    }

    /**
     * 上报一些核心日志信息至api网关, 每个微服务记录的日志长度不超过500字符
     *
     * @param log
     */
    public static void appendServiceLog(String log) {
        if (log != null && log.length() > 0) {
            RpcContext context = RpcContext.getContext();
            String tmp = context.getNotification(ConstField.SERVICE_LOG);
            if (tmp != null && tmp.length() > 0) {
                log = tmp + LOG_SPLITTER + log;
            }
            if (log.length() > 500) {
                logger.warn("service log is too long." + log);
                context.setNotification(ConstField.SERVICE_LOG, log.substring(0, 500));
            } else {
                context.setNotification(ConstField.SERVICE_LOG, log);
            }
        }
    }
}
