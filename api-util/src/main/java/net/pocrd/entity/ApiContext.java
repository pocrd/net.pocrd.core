package net.pocrd.entity;

import com.alibaba.dubbo.rpc.protocol.thrift.io.RandomAccessByteArrayOutputStream;
import net.pocrd.core.LocalException;
import net.pocrd.define.SerializeType;
import net.pocrd.responseEntity.AuthenticationResult;
import net.pocrd.responseEntity.KeyValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Api请求上下文信息
 */
public class ApiContext {
    private static final Logger logger = LoggerFactory.getLogger(ApiContext.class);

    public final Pattern callbackRegex = Pattern.compile("^[A-Za-z]\\w{5,64}$");

    /**
     * 接口调用列表
     */
    public List<ApiMethodCall> apiCalls = null;

    /**
     * 二级调用资源描述
     */
    public List<ApiMethodCall> lv2ApiCalls = null;

    /**
     * 三级调用资源描述
     */
    public List<ApiMethodCall> lv3ApiCalls = null;

    /**
     * 是否为ssl链接
     */
    public boolean isSSL = false;

    /**
     * 当前调用资源描述
     */
    public ApiMethodCall currentCall = null;

    /**
     * 访问信息
     */
    public Map<String, String> requestInfo;

    /**
     * 在日志记录中忽略敏感信息
     */
    public final void ignoreParameterForSecurity(String key) {
        if (requestInfo != null) {
            requestInfo.remove(key);
        }
    }

    public final String recoverRequestBody() {
        StringBuilder sb = new StringBuilder(100);
        if (requestInfo != null) {
            try {
                for (String key : requestInfo.keySet()) {
                    if (key != null) {
                        sb.append(key);
                        sb.append("=");
                        sb.append(URLEncoder.encode(requestInfo.get(key), "UTF-8"));
                        sb.append("&");
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("URLEncoder encode the post data failad", e);
            }
        }
        return sb.toString();
    }

    public final String getRequestString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(isSSL ? "https://" : "http://");
        sb.append(host);
        sb.append("/m.api?");
        sb.append(recoverRequestBody());
        return sb.toString();
    }

    /**
     * 用户账号,日志用
     */
    public String uid;

    /**
     * http请求的标识符
     */
    public String cid;

    /**
     * 设备序列号,业务用
     */
    public long deviceId;

    /**
     * 设备序列号,日志用
     */
    public String deviceIdStr;

    /**
     * 客户端应用版本号
     */
    public String versionCode;

    /**
     * 应用编号,显示传参的_aid
     */
    public String appid;

    /**
     * 第三方合作者编号
     */
    public String thirdPartyId;

    /**
     * 返回值序列化方式
     */
    public SerializeType format = SerializeType.JSON;

    /**
     * 返回消息的语言
     */
    public String location;

    /**
     * 访问时间
     */
    public long startTime = 0;

    /**
     * 时间开销
     */
    public int costTime;

    /**
     * 客户端信息
     */
    public String agent;

    /**
     * http referer
     */
    public String referer;

    /**
     * 访问站点
     */
    public String host;

    /**
     * 清除用戶cookie中的token信息
     */
    public boolean clearUserToken = false;

    /**
     * 清除已经过期的token
     */
    public boolean clearExpiredUserToken = false;

    /**
     * 客户端IP
     */
    public String clientIP;

    /**
     * Token
     */
    public String token;

    /**
     * secret token 用于在不同domian间传递csrftoken, 只能在https协议下传入
     */
    public String stoken;

    /**
     * Security Level 本次调用所需的综合安全级别
     */
    public int requiredSecurity;

    /**
     * 调用者信息
     */
    public CallerInfo caller;

    /**
     * 扩展的调用者信息, 一般由 SubSystem 自行签发
     */
    public ExtensionCallerInfo extCaller;

    /**
     * 已进行序列化的method call计数, 用于接口合并调用时分段下发返回值
     */
    public int serializeCount;

    /**
     * 错误数据
     */
    public LocalException localException;

    /**
     * 线程相关的序列化数据缓冲池，用于暂存序列化数据
     */
    public RandomAccessByteArrayOutputStream outputStream = new RandomAccessByteArrayOutputStream(4096);

    /**
     * jsonp回调信息
     */
    public byte[] jsonpCallback = null;

    /**
     * 子系统授权接口调用
     */
    public ApiMethodCall authCall = null;

    /**
     * 子系统授权结果
     */
    public AuthenticationResult authResult = null;

    /**
     * 返回给客户端的额外消息
     */
    private Map<String, KeyValuePair> notifications = new HashMap<String, KeyValuePair>();

    /**
     * 客户端传上来的 cookie
     */
    private Map<String, String> cookies = new HashMap<String, String>();

    /**
     * 添加 cookie
     *
     * @param key
     * @param value
     */
    public final void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    /**
     * 获取 cookie 值
     *
     * @param key
     */
    public final String getCookie(String key) {
        return cookies.get(key);
    }

    /**
     * 不存储重复的key
     *
     * @param n notification
     */
    public final void addNotification(KeyValuePair n) {
        if (n == null)
            return;
        if (!notifications.containsKey(n.key)) {
            notifications.put(n.key, n);
        }
    }

    public final void clearNotification() {
        notifications.clear();
    }

    public final List<KeyValuePair> getNotifications() {
        return new ArrayList<KeyValuePair>(notifications.values());
    }

    /**
     * 清除变量信息
     */
    public final void clear() {
        this.agent = null;
        this.apiCalls = null;
        this.lv2ApiCalls = null;
        this.lv3ApiCalls = null;
        this.appid = null;
        this.caller = null;
        this.extCaller = null;
        this.cid = null;
        this.clearUserToken = false;
        this.clearExpiredUserToken = false;
        this.clientIP = null;
        this.cookies.clear();
        this.costTime = 0;
        this.currentCall = null;
        this.deviceId = 0;
        this.deviceIdStr = null;
        this.format = SerializeType.JSON;
        this.host = null;
        this.isSSL = false;
        this.jsonpCallback = null;
        this.authCall = null;
        this.authResult = null;
        this.localException = null;
        this.location = null;
        this.notifications.clear();
        this.outputStream.reset();
        this.referer = null;
        this.requiredSecurity = 0;
        this.requestInfo = null;
        this.serializeCount = 0;
        this.startTime = 0;
        this.stoken = null;
        this.thirdPartyId = null;
        this.token = null;
        this.uid = null;
        this.versionCode = null;
        MDC.clear();
    }
}