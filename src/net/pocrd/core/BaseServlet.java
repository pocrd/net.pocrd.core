package net.pocrd.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.pocrd.define.CommonParameter;
import net.pocrd.define.SerializeType;
import net.pocrd.entity.ApiContext;
import net.pocrd.entity.ApiMethodCall;
import net.pocrd.entity.CallerInfo;
import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCodeException;
import net.pocrd.util.CommonConfig;
import net.pocrd.util.HMacHelper;
import net.pocrd.util.TokenHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public abstract class BaseServlet extends HttpServlet {
    private static final long     serialVersionUID = 1L;
    private static final Logger   logger           = LogManager.getLogger(BaseServlet.class.getName());
    protected static final Logger access           = LogManager.getLogger("net.pocrd.api.access");
    protected static final String DEBUG_AGENT      = "pocrd.tester";
    protected HMacHelper          hmac             = HMacHelper.getInstance();

    private static ObjectMapper   json             = new ObjectMapper();
    protected static ApiManager   apiManager;
    private static TokenHelper    tokenHelper      = new TokenHelper(CommonConfig.Instance.tokenPwd);

    static {
        json.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        json.addMixInAnnotations(com.google.protobuf.GeneratedMessage.class, Mixin1.class);
        json.addMixInAnnotations(com.google.protobuf.MessageOrBuilder.class, Mixin2.class);
        json.setFilters(new SimpleFilterProvider().addFilter("global", SimpleBeanPropertyFilter.serializeAllExcept("unknownFields",
                "defaultInstanceForType", "descriptorForType", "allFields", "serializedSize", "initialized")));

    }

    public static void registerAll(String packageName) {
        apiManager = new ApiManager(packageName);
    }

    @JsonFilter("global")
    static interface Mixin1 {

    }

    @JsonIgnoreType()
    static interface Mixin2 {

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        boolean fatalError = false;
        boolean accessDenied = false;
        ApiContext apiContext = null;
        try {
            apiContext = ApiContext.getCurrent();
            apiContext.clear();
            apiContext.startTime = System.currentTimeMillis();
            parseParameter(apiContext, request);
            accessDenied = parseMethodInfo(apiContext, request);
        } catch (Exception e) {
            logger.error("init request failed.", e);
            fatalError = true;
        }

        try {
            // TODO:签名验证失败作为fatal error处理
            if (fatalError || accessDenied) {
                access.info(apiContext.getStringInfo());
            } else {
                for (ApiMethodCall call : apiContext.apiCallInfos) {
                    if (call == ApiMethodCall.UnknownMethodCall) {
                        access.info("0  " + call.method.methodName + "  " + apiContext.getStringInfo());
                        continue;
                    }
                    apiContext.currentCall = call;
                    executeApiCall(call, apiContext, response);
                    long currentTime = System.currentTimeMillis();
                    call.costTime = (int)(currentTime - apiContext.startTime);
                    apiContext.startTime = currentTime;
                    // TODO:记录访问的返回字节数(未压缩)
                    access.info(call.costTime + "  " + call.method.methodName + "  " + apiContext.getStringInfo());
                }
            }
        } catch (Exception e) {
            logger.error("api execute error.", e);
        } finally {
            // TODO:进行流式输出
            apiContext.clear();
            if (fatalError) {
                // 错误请求
                response.setStatus(400);
            } else if (accessDenied) {
                // 访问被拒绝
                response.setStatus(401);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void parseParameter(ApiContext context, HttpServletRequest request) {
        {
            context.agent = request.getHeader("User-Agent");
            context.clientIP = request.getRemoteAddr();
        }
        {
            String cid = request.getHeader("_cid");
            if (cid != null) {
                context.agent = "(" + cid + ")" + context.agent;
            }
        }
        {
            String httpMethod = request.getMethod();
            if ("POST".equalsIgnoreCase(httpMethod)) {
                StringBuilder sb = new StringBuilder(128);
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
            String functionFlags = request.getParameter(CommonParameter.ff.toString());
            if (functionFlags == null || functionFlags.length() == 0) {
                context.functionFlags = null;
            } else {
                context.functionFlags = new HashSet<String>(Arrays.asList(functionFlags.split(",")));
            }
        }
        {
            String format = request.getParameter(CommonParameter.ft.toString());
            if (format != null && format.length() > 0) {
                format = format.toLowerCase();
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
            context.location = request.getParameter(CommonParameter.lo.toString());
        }
        {
            String token = request.getParameter(CommonParameter.tk.toString());
            if (token != null && token.length() > 0) {
                context.token = token;
                try {
                    context.caller = tokenHelper.parse(token);

                    if (CommonConfig.isDebug) {
                        if (context.caller == null && (context.agent != null && context.agent.contains(DEBUG_AGENT))) {
                            context.caller = CallerInfo.TESTER;
                        }
                    }

                    if (context.caller != null) {
                        // TODO:fill device info into caller
                        context.sn = Long.toString(context.caller.sn);
                        context.uid = Long.toString(context.caller.uid);
                    }
                } catch (RuntimeException e) {
                    logger.error("get device info failed.", e);
                }
            }

            if (context.sn == null) {
                String c_sn = request.getParameter("c_sn");
                if (c_sn != null && c_sn.length() > 0) {
                    // 用以区分从token中解析出的sn
                    context.sn = "-" + c_sn;
                }
            }

            if (context.uid == null) {
                String c_uid = request.getParameter("c_uid");
                if (c_uid != null && c_uid.length() > 0) {
                    // 用以区分从token中解析出的uid
                    context.uid = "-" + c_uid;
                }
            }
        }
    }

    private void executeApiCall(ApiMethodCall call, ApiContext apiContext, HttpServletResponse response) {
        try {
            call.result = apiManager.processRequest(call.method.methodName, call.parameters);
            call.setReturnCode(ReturnCode.SUCCESS);
        } catch (ReturnCodeException rce) {
            call.setReturnCode(rce.getCode());
            logger.error(rce);
        } catch (Exception e) {
            call.setReturnCode(ReturnCode.UNKNOWN_ERROR);
            logger.error(e);
        }

        ReturnCode code = call.getReturnCode();
        if (code.getCode() > 0 && Arrays.binarySearch(call.method.errorCodes, code.getCode()) < 0) {
            logger.error("未预料的错误返回码 " + call.getReturnCode());
            ReturnCode shadow = code.getShadow();
            if (shadow != null) {
                if (CommonConfig.isDebug) {
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
     * 解析调用方法。
     * 
     * @param context
     * @param request
     * @return true if access denied, otherwise false.
     */
    public abstract boolean parseMethodInfo(ApiContext context, HttpServletRequest request);
}
