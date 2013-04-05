package net.pocrd.entity;

import java.util.ArrayList;

import net.pocrd.define.SerializeType;

/**
 * Api请求上下文信息
 */
public class ApiContext {
    /**
     * 当前线程的ApiContext对象
     */
    private static ApiContext current;

    /**
     * 获取当前Api上下文
     */
    public static ApiContext getCurrent() {
        if (current == null) {
            current = new ApiContext();
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
    public String                   userid;

    /**
     * 设备序列号,日志用
     */
    public String                   sn;

    /**
     * 二进制码串，根据客户端上传的功能码16进制码解析得到，且对应二进制左起第一位为1(也做开始标识位，含义：normal，获取普通书籍)。 为了支持更多的位扩展,改码值时总是进行字符串操作。
     * 解析过程为：客户端16进制码=>Version(2进制码串)=>2进制码串从左向右按位匹配FilterVersion中枚举的所有功能项(枚举int值代表的是在version码中中从左起对应的位数)
     * 例如：FilterVersion中枚举了{1(普通书),2,3,4,5..8},客户端上传"F1"解析得到的Version(二进制码)11110001,即支持功能1、2、3、4、8
     */
    public String                   ver;

    /**
     * 是否是POST请求
     */
    public boolean                  isPost;

    /**
     * 返回值序列化方式
     */
    public SerializeType            format       = SerializeType.XML;

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
     * token异常 0为正常，1为过期，2为解析失败
     */
    public int                      tokenError   = 0;

    /**
     * 导致逻辑无法执行的严重错误
     */
    public int                      fatalError   = 0;

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
        sb.append(userid);
        sb.append("  ");
        sb.append(sn);
        sb.append("  ");
        return sb.toString();
    }

    /**
     * 清除变量信息
     */
    public final void clear() {
        this.agent = null;
        this.apiCallInfos = null;
        this.caller = null;
        this.clientIP = null;
        this.currentCall = null;
        this.fatalError = 0;
        this.format = SerializeType.XML;
        this.isPost = false;
        this.location = null;
        this.requestInfo = null;
        this.sn = null;
        this.startTime = 0;
        this.token = null;
        this.tokenError = 0;
        this.userid = null;
        this.ver = null;
    }
}