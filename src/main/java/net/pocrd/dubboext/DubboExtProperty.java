package net.pocrd.dubboext;

import com.alibaba.fastjson.JSON;
import net.pocrd.define.ConstField;
import net.pocrd.define.Serializer.ApiSerializerFeature;
import net.pocrd.entity.AbstractReturnCode;
import net.pocrd.responseEntity.CreditNotification;
import net.pocrd.responseEntity.MessageNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by rendong on 14-5-15.
 */
public class DubboExtProperty {
    private static       Logger                           logger               = LoggerFactory.getLogger(DubboExtProperty.class);
    private static final String                           JSONOBJECT_SEPARATOR = ",";
    public static final  String                           LOG_SPLITTER         = new String(new char[] { ' ', 2 });
    //非dubbo provider调用用来暂存notification,例如:单元测试等
    final static         ThreadLocal<Map<String, String>> notifications        = new ThreadLocal<Map<String, String>>();

    static void addNotifications(String key, String value) {
        Map<String, String> map = notifications.get();
        if (map == null) {
            map = new HashMap<String, String>();
            notifications.set(map);
        }
        map.put(key, value);
    }

    //do value copy
    public static void addNotifications(Map<String, String> rpcMap) {
        if (rpcMap != null && !rpcMap.isEmpty()) {
            Map<String, String> map = notifications.get();
            if (map == null) {
                map = new HashMap<String, String>();
                notifications.set(map);
            }
            for (Entry<String, String> entry : rpcMap.entrySet()) {
                if (ConstField.ERROR_CODE_EXT.equals(entry.getKey())) {
                    //不使用provider的errorcode替换自身的
                    String currentErrorCode = map.get(entry.getKey());
                    if (currentErrorCode != null && !currentErrorCode.isEmpty()) {
                        logger.info(
                                "provider return an error code ,but current service has set an error code.current error code:{},return error code:{}",
                                currentErrorCode, entry.getValue());
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                } else if (ConstField.CREDIT.equals(entry.getKey()) || ConstField.MSG.equals(entry.getKey())) {
                    //累加通知
                    String tmp = map.get(entry.getKey());
                    if (tmp == null) {
                        map.put(entry.getKey(), entry.getValue());
                    } else {
                        map.put(entry.getKey(), tmp + JSONOBJECT_SEPARATOR + entry.getValue());
                    }
                } else if (ConstField.SERVICE_LOG.equals(entry.getKey())) {
                    String tmp = map.get(entry.getKey());
                    if (tmp == null) {
                        map.put(entry.getKey(), entry.getValue());
                    } else {
                        map.put(entry.getKey(), tmp + LOG_SPLITTER + entry.getValue());
                    }
                } else {
                    //其他类型进行覆盖
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static Map<String, String> getCurrentNotifications() {
        return notifications.get();
    }

    static String getValue(String key) {
        Map<String, String> map = notifications.get();
        return map == null ? null : map.get(key);
    }

    static boolean containsKey(String key) {
        Map<String, String> map = notifications.get();
        return map == null ? false : map.containsKey(key);
    }

    /**
     * 用于在返回正常业务值的同时返回一个错误code, 请使用throw ServiceException / ServiceRuntimeException 来返回错误
     * 慎用!
     *
     * @deprecated
     */
    @Deprecated
    public static void setErrorCodeExt(AbstractReturnCode code) {
        if (code != null) {
            if (!containsKey(ConstField.ERROR_CODE_EXT)) {
                addNotifications(ConstField.ERROR_CODE_EXT, String.valueOf(code.getCode()));
            }
        }
    }

    /**
     * 用于在返回正常业务值的同时返回一个错误code, 请使用throw ServiceException / ServiceRuntimeException 来返回错误
     * 慎用!
     *
     * @deprecated
     */
    @Deprecated
    public static String getErrorCodeExt() {
        return getValue(ConstField.ERROR_CODE_EXT);
    }

    /**
     * 覆盖写入token信息 以及 stoken信息 和 stoken的过期时间
     *
     * @param token
     * @param stoken
     * @param stkDuration
     */
    public static void setCookieToken(String token, String stoken, int stkDuration) {
        if (token != null) {
            addNotifications(ConstField.SET_COOKIE_TOKEN, token);
        }
        if (stoken != null) {
            addNotifications(ConstField.SET_COOKIE_STOKEN, stoken + "|" + stkDuration);
        }
    }

    /**
     * 覆盖写入token信息 以及 stoken信息
     *
     * @param token
     * @param stoken
     */
    public static void setCookieToken(String token, String stoken) {
        if (token != null) {
            addNotifications(ConstField.SET_COOKIE_TOKEN, token);
        }
        if (stoken != null) {
            addNotifications(ConstField.SET_COOKIE_STOKEN, stoken);
        }
    }

    public static String getCookieToken() {
        return getValue(ConstField.SET_COOKIE_TOKEN);
    }

    public static String getSecretCookieToken() {
        return getValue(ConstField.SET_COOKIE_STOKEN);
    }

    /**
     * 覆盖写入用户信息
     *
     * @param info
     */
    public static void setCookieUserInfo(String info) {
        if (info != null) {
            addNotifications(ConstField.SET_COOKIE_USER_INFO, info);
        }
    }

    public static String getCookieUserInfo() {
        return getValue(ConstField.SET_COOKIE_USER_INFO);
    }

    /**
     * 覆盖
     *
     * @param url
     */
    public static void setRedirectUrl(String url) {
        if (url != null) {
            addNotifications(ConstField.REDIRECT_TO, url);
        }
    }

    public static String getRedirectUrl() {
        return getValue(ConstField.REDIRECT_TO);
    }

    /**
     * 累加
     *
     * @param creditInfo
     */
    public static void addCreditsGain(CreditNotification creditInfo) {
        if (creditInfo != null) {
            String tmp = getValue(ConstField.CREDIT);
            if (tmp == null) {
                addNotifications(ConstField.CREDIT, JSON.toJSONString(creditInfo, ApiSerializerFeature.SERIALIZER_FEATURES));
            } else {
                addNotifications(ConstField.CREDIT,
                        tmp + JSONOBJECT_SEPARATOR + JSON.toJSONString(creditInfo, ApiSerializerFeature.SERIALIZER_FEATURES));
            }
        }
    }

    public static String getCreditsGain() {
        return getValue(ConstField.CREDIT);
    }

    /**
     * 累加
     *
     * @param msgInfo
     */
    public static void addMessageInfo(MessageNotification msgInfo) {
        if (msgInfo != null) {
            String tmp = getValue(ConstField.MSG);
            if (tmp == null) {
                addNotifications(ConstField.MSG, JSON.toJSONString(msgInfo, ApiSerializerFeature.SERIALIZER_FEATURES));
            } else {
                addNotifications(ConstField.MSG, tmp + JSONOBJECT_SEPARATOR + JSON.toJSONString(msgInfo, ApiSerializerFeature.SERIALIZER_FEATURES));
            }
        }
    }

    public static String getMessageInfo() {
        return getValue(ConstField.MSG);
    }

    public static void clearNotificaitons() {
        Map map = notifications.get();
        if (map != null && !map.isEmpty()) {
            map.clear();
        }
    }

    /**
     * 上报一些核心日志信息至api,日志长度不超过500字符
     *
     * @param log
     */
    public static void appendServiceLog(String log) {
        if (log != null) {
            String tmp = getValue(ConstField.SERVICE_LOG);
            if (tmp == null) {
                if (log.length() > 500) {
                    logger.warn("append log failed ,length of log is large than 500 ,actually length:{}", log.length());
                } else {
                    addNotifications(ConstField.SERVICE_LOG, log);
                }
            } else {
                StringBuilder sb = new StringBuilder(tmp);
                sb.append(LOG_SPLITTER).append(log);
                if (log.length() > 500) {
                    logger.warn("append log failed ,length of log is large than 500 ,actually length:{}", log.length());
                } else {
                    addNotifications(ConstField.SERVICE_LOG, sb.toString());
                }
            }
        }
    }

    public static String getAppendServiceLog() {
        return getValue(ConstField.SERVICE_LOG);
    }

}
