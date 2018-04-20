package net.pocrd.dubboext;

/**
 * Created by rendong on 2017/7/11.
 */
public class TraceInfo {
    private static ThreadLocal<TraceInfo> info = new ThreadLocal<>();

    public TraceInfo() {
    }

    public TraceInfo(String traceid, String sysinfo, String userinfo) {
        this.traceid = traceid;
        this.sysinfo = sysinfo;
        this.userinfo = userinfo;
    }

    /**
     * 调用串号,由调用起始点生成
     */
    public String traceid;

    /**
     * 源系统信息,标识调用起始点 可以是appid|deviceId|versionCode|clientIP或者是服务名称|serverIP
     */
    public String sysinfo;

    /**
     * 用户信息 userid|oauthid|extinfo
     */
    public String userinfo;

    public static void setTraceInfo(TraceInfo tinfo) {
        info.set(tinfo);
    }

    public static TraceInfo getTraceInfo() {
        return info.get();
    }

    public static void clear() {
        info.remove();
    }
}
