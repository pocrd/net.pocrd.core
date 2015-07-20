package net.pocrd.entity;

import net.pocrd.core.LocalException;
import net.pocrd.define.SerializeType;
import net.pocrd.responseEntity.KeyValuePair;
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Api请求上下文信息
 */
public class ApiContext {
    /**
     * 当前线程的ApiContext对象
     */
    private static ThreadLocal<ApiContext> threadLocal = new ThreadLocal<ApiContext>();

    /**
     * 获取当前Api上下文
     */
    public static ApiContext getCurrent() {
        ApiContext current = threadLocal.get();
        if (current == null) {
            current = new ApiContext();
            threadLocal.set(current);
        }
        return current;
    }

    private ApiContext() {
    }

    public Pattern callbackRegex = Pattern.compile("^[A-Za-z]\\w{0,7}$");

    /**
     * 调用资源描述
     */
    public ArrayList<ApiMethodCall> apiCallInfos = null;

    /**
     * 当前调用资源描述
     */
    public ApiMethodCall currentCall = null;

    /**
     * 访问信息
     */
    public String requestInfo;

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
    public int appid;

    /**
     * 第三方合作者编号
     */
    public int thirdPartyId;

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
     * 客户端信息
     */
    public String agent;

    /**
     * 访问站点
     */
    public String host;

    /**
     * 是否清除用戶 token
     */
    public boolean clearUserToken = false;

    /**
     * 是否清除用戶 token 标志位
     */
    public boolean clearUserTokenFlag = false;

    /**
     * 客户端IP
     */
    public String clientIP;

    /**
     * Device Token
     */
    public String deviceToken;

    /**
     * Token
     */
    public String token;

    /**
     * Security Level 本次调用所需的综合安全级别
     */
    public int requiredSecurity;

    /**
     * 调用者信息
     */
    public CallerInfo caller;

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
    public ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);

    /**
     * jsonp回调信息
     */
    public String jsonpCallback = null;

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
     * @return
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
        this.apiCallInfos = null;
        this.appid = 0;
        this.thirdPartyId = 0;
        this.caller = null;
        this.cid = null;
        this.clearUserToken = false;
        this.clearUserTokenFlag = false;
        this.clientIP = null;
        this.currentCall = null;
        this.deviceId = 0;
        this.deviceIdStr = null;
        this.format = SerializeType.JSON;
        this.location = null;
        this.requestInfo = null;
        this.serializeCount = 0;
        this.startTime = 0;
        this.token = null;
        this.deviceToken = null;
        this.uid = null;
        this.versionCode = null;
        this.outputStream.reset();
        this.notifications.clear();
        this.requiredSecurity = 0;
        this.localException = null;
        this.host = null;
        this.cookies.clear();
        MDC.clear();
    }
}