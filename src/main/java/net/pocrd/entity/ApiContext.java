package net.pocrd.entity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import net.pocrd.define.SerializeType;

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

    private ApiContext() {}

    /**
     * 调用资源描述
     */
    public ArrayList<ApiMethodCall> apiCallInfos = new ArrayList<ApiMethodCall>();

    /**
     * 当前调用资源描述
     */
    public ApiMethodCall            currentCall  = null;

    /**
     * 访问信息
     */
    public String                   requestInfo;

    /**
     * 用户账号,日志用
     */
    public String                   uid;

    /**
     * http请求的标识符
     */
    public String                   cid;

    /**
     * 设备序列号,日志用
     */
    public String                   deviceId;

    /**
     * 客户端应用版本号
     */
    public String                   versionCode;

    /**
     * 应用编号
     */
    public String                   appid;

    /**
     * 返回值序列化方式
     */
    public SerializeType            format       = SerializeType.JSON;

    /**
     * 返回消息的语言
     */
    public String                   location;

    /**
     * 访问时间
     */
    public long                     startTime    = 0;

    /**
     * 客户端信息
     */
    public String                   agent;

    /**
     * 客户端IP
     */
    public String                   clientIP;

    /**
     * Token
     */
    public String                   token;

    /**
     * 调用者信息
     */
    public CallerInfo               caller;

    /**
     * 已进行序列化的method call计数, 用于接口合并调用时分段下发返回值
     */
    public int                      serializeCount;

    /**
     * 线程相关的序列化数据缓冲池，用于暂存序列化数据
     */
    public ByteArrayOutputStream    outputStream = new ByteArrayOutputStream(4096);

    /**
     * 返回string格式的信息摘要
     * 
     * @return
     */
    public final String getStringInfo() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(requestInfo);
        sb.append("  ");
        sb.append(agent);
        sb.append("  ");
        sb.append(clientIP);
        sb.append("  ");
        return sb.toString();
    }

    /**
     * 清除变量信息
     */
    public final void clear() {
        this.agent = null;
        this.apiCallInfos = null;
        this.appid = null;
        this.caller = null;
        this.cid = null;
        this.clientIP = null;
        this.currentCall = null;
        this.deviceId = null;
        this.format = SerializeType.JSON;
        this.location = null;
        this.requestInfo = null;
        this.serializeCount = 0;
        this.startTime = 0;
        this.token = null;
        this.uid = null;
        this.versionCode = null;
        this.outputStream.reset();
    }
}