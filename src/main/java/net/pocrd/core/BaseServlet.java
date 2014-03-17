package net.pocrd.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.pocrd.define.CommonParameter;
import net.pocrd.define.CompileConfig;
import net.pocrd.define.ConstField;
import net.pocrd.define.SerializeType;
import net.pocrd.entity.ApiContext;
import net.pocrd.entity.ApiMethodCall;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.CallerInfo;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.KeyValuePair;
import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCodeException;
import net.pocrd.util.MiscUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public abstract class BaseServlet extends HttpServlet {
    private static final long              serialVersionUID        = 1L;
    private static final Logger            logger                  = LogManager.getLogger(BaseServlet.class);
    protected static final Marker          SERVLET_MARKER          = MarkerManager.getMarker("servlet");

    protected static final Logger          access                  = CommonConfig.getInstance().accessLogger;
    protected static final String          DEBUG_AGENT             = "pocrd.tester";
    protected static ApiManager            apiManager;
    protected static final ApiMethodCall[] EMPTY_METHOD_CALL_ARRAY = new ApiMethodCall[0];

    public static final byte[]             XML_START               = "<xml>".getBytes(ConstField.UTF8);
    public static final byte[]             XML_END                 = "</xml>".getBytes(ConstField.UTF8);
    public static final byte[]             XML_EMPTY               = "<empty/>".getBytes(ConstField.UTF8);
    public static final byte[]             JSON_STAT               = "{\"stat\":".getBytes(ConstField.UTF8);
    public static final byte[]             JSON_CONTENT            = ",\"content\":[".getBytes(ConstField.UTF8);
    public static final byte[]             JSON_SPLIT              = ",".getBytes(ConstField.UTF8);
    public static final byte[]             JSON_END                = "]}".getBytes(ConstField.UTF8);
    public static final byte[]             JSON_EMPTY              = "{}".getBytes(ConstField.UTF8);

    protected Exception                    outputException         = null;

    /**
     * 注册api接口，该函数需要在应用程序启动完成前结束工作
     * 
     * @param packageName
     * @param entityPrefix
     */
    public static void registerAll(String packageName, String entityPrefix) {
        apiManager = new ApiManager(packageName, entityPrefix);
    }

    public static ApiMethodInfo[] getApiInfos() {
        return apiManager.getApiMethodInfos();
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        boolean fatalError = false;
        ReturnCode parseResult = null;
        ApiContext apiContext = null;
        try {
            apiContext = ApiContext.getCurrent();
            apiContext.clear();
            apiContext.startTime = System.currentTimeMillis();
            parseParameter(apiContext, request);
            parseResult = parseMethodInfo(apiContext, request);
        } catch (Exception e) {
            logger.error(SERVLET_MARKER, "init request failed.", e);
            fatalError = true;
        }

        try {
            if (fatalError || parseResult != ReturnCode.SUCCESS) {
                access.info(apiContext.getStringInfo());
            } else {
                int count = 0;
                for (ApiMethodCall call : apiContext.apiCallInfos) {
                    if (call == ApiMethodCall.UnknownMethodCall) {
                        access.info("0  " + call.method.methodName + "  " + apiContext.getStringInfo());
                        continue;
                    }
                    apiContext.currentCall = call;
                    call.startTime = (count++ == 0) ? apiContext.startTime : System.currentTimeMillis();
                    executeApiCall(call, apiContext, response);
                    call.costTime = (int)(System.currentTimeMillis() - call.startTime);
                    serializeCallResult(apiContext, request, response, call);
                    access.info(call.costTime + "  " + call.method.methodName + "  " + apiContext.getStringInfo());
                }
            }
        } catch (Exception e) {
            logger.error(SERVLET_MARKER, "api execute error.", e);
        } finally {
            try {
                if (fatalError || parseResult == ReturnCode.REQUEST_PARSE_ERROR) {
                    // 错误请求
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                } else if (parseResult != ReturnCode.SUCCESS) {
                    // 访问被拒绝(如签名验证失败)
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                } else {
                    Exception e = output(apiContext, apiContext.apiCallInfos.toArray(EMPTY_METHOD_CALL_ARRAY), request, response);
                    if (e != null) {
                        logger.error(SERVLET_MARKER, "output failed.", e);
                    }
                }
            } catch (Exception e) {
                logger.error(SERVLET_MARKER, "output failed.", e);
            }
            apiContext.clear();
        }
    }

    abstract protected Exception output(ApiContext apiContext, ApiMethodCall[] calls, HttpServletRequest request, HttpServletResponse response);

    abstract protected void serializeCallResult(ApiContext apiContext, HttpServletRequest request, HttpServletResponse response, ApiMethodCall call);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void parseParameter(ApiContext context, HttpServletRequest request) {
        {
            context.agent = request.getHeader("User-Agent");
            context.clientIP = MiscUtil.getClientIP(request);
            context.cid = request.getHeader(CommonParameter.callId);
            if (context.cid != null && context.cid.length() > 16) {
                context.cid = null;
            }
            if (context.cid == null) {
                context.cid = "s:" + context.startTime;
            }
            context.appid = request.getParameter(CommonParameter.applicationId);
            context.versionCode = request.getParameter(CommonParameter.versionCode);
            context.deviceId = request.getParameter(CommonParameter.deviceId);
            context.uid = request.getParameter(CommonParameter.userId);
        }
        {
            String httpMethod = request.getMethod();
            if ("POST".equalsIgnoreCase(httpMethod)) {
                StringBuilder sb = new StringBuilder(256);
                Enumeration<String> keys = request.getParameterNames();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    if (key != null) {
                        sb.append(key);
                        sb.append("=");
                        sb.append(request.getParameter(key));
                        sb.append("&");
                    }
                }
                context.requestInfo = sb.toString();
            } else {
                context.requestInfo = request.getQueryString();
            }
        }
        {
            String format = request.getParameter(CommonParameter.format);
            if (format != null && format.length() > 0) {
                if (format.equals("xml")) {
                    context.format = SerializeType.XML;
                } else if (format.equals("protobuf")) {
                    context.format = SerializeType.PROTOBUF;
                } else if (format.equals("json")) {
                    context.format = SerializeType.JSON;
                } else {
                    context.format = SerializeType.JSON;
                }
            } else {
                context.format = SerializeType.JSON;
            }
        }
        {
            context.location = request.getParameter(CommonParameter.location);
        }
        {
            context.token = request.getParameter(CommonParameter.token);
            try {
                context.caller = parseCaller(context, context.token);

                if (CompileConfig.isDebug) {
                    if (context.caller == null && (context.agent != null && context.agent.contains(DEBUG_AGENT))) {
                        context.caller = CallerInfo.TESTER;
                    }
                }
            } catch (RuntimeException e) {
                logger.error(SERVLET_MARKER, "get device info failed.", e);
            }
        }
    }

    private void executeApiCall(ApiMethodCall call, ApiContext apiContext, HttpServletResponse response) {
        try {
            call.result = apiManager.processRequest(call.method.methodName, call.parameters);
            call.setReturnCode(ReturnCode.SUCCESS);
        } catch (ReturnCodeException rce) {
            call.setReturnCode(rce.getCode());
            logger.error(SERVLET_MARKER, "servlet catch an error.", rce);
        } catch (Throwable t) {
            call.setReturnCode(ReturnCode.UNKNOWN_ERROR);
            logger.error(SERVLET_MARKER, "unknown error.", t);
        }

        ReturnCode code = call.getReturnCode();
        if (code.getCode() > 0 && Arrays.binarySearch(call.method.errors, code.getCode()) < 0) {
            logger.error(SERVLET_MARKER, "未预料的错误返回码 " + call.getReturnCode());
            ReturnCode shadow = code.getShadow();
            if (shadow != null) {
                if (CompileConfig.isDebug) {
                    if (Arrays.binarySearch(call.method.errorCodes, shadow.getCode()) < 0) {
                        throw new RuntimeException("method isn't export this shadow code. " + shadow.getCode());
                    }
                }
                call.replaceReturnCode(shadow);
            } else {
                call.replaceReturnCode(ReturnCode.UNKNOWN_ERROR);
            }
        }
    }

    /**
     * 解析调用者信息
     * 
     * @param context
     * @param token
     * @return
     */
    abstract protected CallerInfo parseCaller(ApiContext context, String token);

    /**
     * 解析调用方法。
     * 
     * @param context
     * @param request
     * @return 0 success, .
     */
    abstract protected ReturnCode parseMethodInfo(ApiContext context, HttpServletRequest request);
}
