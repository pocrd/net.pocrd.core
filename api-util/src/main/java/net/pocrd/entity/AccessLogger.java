package net.pocrd.entity;

import net.pocrd.core.HttpRequestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * api访问日志
 */
public final class AccessLogger {
    private static       Logger       accessFileLogger  = LoggerFactory.getLogger("net.pocrd.api.access");
    private static       Logger       requestFileLogger = LoggerFactory.getLogger("net.pocrd.api.request");
    public static final  String       ACCESS_SPLITTER   = new String(new char[] { ' ', 1 });
    private static final AccessLogger accessLogger      = new AccessLogger();

    public static AccessLogger getInstance() {
        return accessLogger;
    }

    private AccessLogger() {
    }

    /**
     * no error
     */
    public void logRequest() {
        ApiContext apiContext = HttpRequestExecutor.get().getApiContext();
        requestFileLogger.info(apiContext.getRequestString() + ACCESS_SPLITTER + apiContext.agent + ACCESS_SPLITTER
                + apiContext.clientIP + ACCESS_SPLITTER + apiContext.token + ACCESS_SPLITTER + ACCESS_SPLITTER + ACCESS_SPLITTER
                + "referer:" + apiContext.referer + ACCESS_SPLITTER + apiContext.startTime + ":" + apiContext.costTime);

    }

    /**
     * 打印request 日志
     *
     * @param errorMsg
     * @param data
     */
    public void logRequest(String errorMsg, String data) {
        ApiContext apiContext = HttpRequestExecutor.get().getApiContext();
        requestFileLogger.info(apiContext.getRequestString() + ACCESS_SPLITTER + apiContext.agent + ACCESS_SPLITTER
                + apiContext.clientIP + ACCESS_SPLITTER + apiContext.token + ACCESS_SPLITTER + errorMsg + ACCESS_SPLITTER + data + ACCESS_SPLITTER
                + "referer:" + apiContext.referer + ACCESS_SPLITTER + apiContext.startTime + ":" + apiContext.costTime);
    }

    /**
     * 打印业务访问日志
     */
    public void logAccess(ApiMethodCall call) {
        accessFileLogger.info(call.costTime + ACCESS_SPLITTER + call.method.methodName + ACCESS_SPLITTER + call.getReturnCode() + ACCESS_SPLITTER
                + call.getOriginCode() + ACCESS_SPLITTER + call.resultLen + ACCESS_SPLITTER + call.message.toString() + ACCESS_SPLITTER
                + call.startTime + ":" + (call.serviceLog == null ? "" : call.serviceLog));
    }

}
