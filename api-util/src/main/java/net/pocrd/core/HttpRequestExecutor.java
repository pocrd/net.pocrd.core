package net.pocrd.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.protocol.dubbo.FutureAdapter;
import com.alibaba.fastjson.JSON;
import net.pocrd.define.*;
import net.pocrd.dubboext.NotificationManager;
import net.pocrd.dubboext.TraceInfo;
import net.pocrd.entity.*;
import net.pocrd.responseEntity.AuthenticationResult;
import net.pocrd.responseEntity.CallState;
import net.pocrd.responseEntity.KeyValuePair;
import net.pocrd.responseEntity.Response;
import net.pocrd.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by rendong on 16/8/24.
 * 用于解析http请求并转发到后端dubbo服务接口的处理器
 */
public class HttpRequestExecutor {
    private static final Logger               logger                  = LoggerFactory.getLogger(HttpRequestExecutor.class);
    //debug 模式下识别http header中dubbo.version参数,将请求路由到指定的dubbo服务上
    private static final String               DEBUG_DUBBOVERSION      = "DUBBO-VERSION";
    //debug 模式下识别http header中dubbo.service.ip参数,将请求路由到指定的dubbo服务上
    private static final String               DEBUG_DUBBOSERVICE_URL  = "DUBBO-SERVICE-URL";
    private static final ApiMethodCall[]      EMPTY_METHOD_CALL_ARRAY = new ApiMethodCall[0];
    private static final String               FORMAT_XML              = "xml";
    private static final String               FORMAT_JSON             = "json";
    private static final String               FORMAT_PLAINTEXT        = "plaintext";
    private static final String               SERVER_ADDRESS          = "a:";
    private static final String               THREADID                = "t:";
    private static final String               SPLIT                   = "|";
    private static final String               REQ_TAG                 = "s:";
    private static final String               CONTENT_TYPE_XML        = "application/xml; charset=utf-8";
    private static final String               CONTENT_TYPE_JSON       = "application/json; charset=utf-8";
    private static final String               CONTENT_TYPE_JAVASCRIPT = "application/javascript; charset=utf-8";
    private static final String               CONTENT_TYPE_PLAINTEXT  = "text/plain";
    private static final String               JSONARRAY_PREFIX        = "[";
    private static final String               JSONARRAY_SURFIX        = "]";
    private static final String               USER_AGENT              = "User-Agent";
    private static final String               REFERER                 = "Referer";
    private static final String               DEBUG_AGENT             = "pocrd.tester";
    private static final Serializer<Response> apiResponseSerializer   = POJOSerializerProvider.getSerializer(Response.class);

    private final ApiContext apiContext = new ApiContext();

    public ApiContext getApiContext() {
        return apiContext;
    }

    private RsaHelper      rsaDecryptHelper = null;
    private AESTokenHelper aesTokenHelper   = null;
    private ApiManager     apiManager       = null;

    protected HttpRequestExecutor() {
    }

    private static final ThreadLocal<HttpRequestExecutor> executor = new ThreadLocal<HttpRequestExecutor>();
    private static String ZkAddress;

    public static HttpRequestExecutor get() {
        return executor.get();
    }

    public static boolean createIfNull(ApiManager apiManager, String zkAddress) {
        HttpRequestExecutor exe = executor.get();
        if (exe == null) {
            CommonConfig config = CommonConfig.getInstance();
            ZkAddress = zkAddress;
            try {
                exe = (HttpRequestExecutor)config.getExecutorFactory().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("load http request executor failed. ", e);
            }
            if (config.getRsaDecryptSecret() != null) {
                exe.rsaDecryptHelper = new RsaHelper(null, config.getRsaDecryptSecret());
            }
            exe.aesTokenHelper = new AESTokenHelper(config.getTokenAes());
            exe.apiManager = apiManager;
            executor.set(exe);
            return true;
        }
        return false;
    }

    /**
     * 执行web请求
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response) {
        CommonConfig config = CommonConfig.getInstance();
        boolean fatalError = false;
        AbstractReturnCode parseResult = null;
        long current = System.currentTimeMillis();
        try {
            MDC.clear();
            apiContext.clear();
            apiContext.startTime = current;
            parseCommonParameter(request, response);
            setResponseHeader(request, response);
            parseResult = parseMethodInfo(request);
            // 验证token是否过期
            if (parseResult == ApiReturnCode.SUCCESS && apiContext.caller != null
                    && SecurityType.expirable(apiContext.requiredSecurity)) {
                if (apiContext.caller.expire < current) {
                    parseResult = ApiReturnCode.TOKEN_EXPIRE;
                    apiContext.clearExpiredUserToken = true;
                }
            }
        } catch (Exception e) {
            logger.error("init request failed.", e);
            fatalError = true;
        }
        try {
            AccessLogger access = AccessLogger.getInstance();
            // 参数解析失败
            if (fatalError) {
                access.logRequest("with fatal error", String.valueOf(ApiReturnCode.FATAL_ERROR.getCode()));
            } else if (parseResult != ApiReturnCode.SUCCESS) {
                access.logRequest("with error", String.valueOf(parseResult.getCode()));
            } else { // 参数解析成功
                List<ApiMethodCall> lv1ApiCalls = null;
                try {
                    if (apiContext.lv2ApiCalls == null) {
                        lv1ApiCalls = apiContext.apiCalls;
                    } else {
                        lv1ApiCalls = new ArrayList<>(apiContext.apiCalls.size());
                        for (ApiMethodCall call : apiContext.apiCalls) {
                            if (call.dependencies == null) {
                                lv1ApiCalls.add(call);
                            }
                        }
                    }
                    executeAllApiCall(lv1ApiCalls, request, response);
                    if (apiContext.lv2ApiCalls != null) {
                        executeAllApiCall(apiContext.lv2ApiCalls, request, response);
                    }
                    if (apiContext.lv3ApiCalls != null) {
                        executeAllApiCall(apiContext.lv3ApiCalls, request, response);
                    }
                    for (ApiMethodCall call : apiContext.apiCalls) {
                        serializeCallResult(call);
                    }
                } finally {
                    apiContext.costTime = (int)(System.currentTimeMillis() - apiContext.startTime);
                    for (ApiMethodCall call : lv1ApiCalls) {
                        MDC.put(CommonParameter.method, call.method.methodName);
                        // access log
                        access.logAccess(call);
                    }
                    if (apiContext.lv2ApiCalls != null) {
                        for (ApiMethodCall call : apiContext.lv2ApiCalls) {
                            MDC.put(CommonParameter.method, call.method.methodName);
                            // access log
                            access.logAccess(call);
                        }
                    }
                    if (apiContext.lv3ApiCalls != null) {
                        for (ApiMethodCall call : apiContext.lv3ApiCalls) {
                            MDC.put(CommonParameter.method, call.method.methodName);
                            // access log
                            access.logAccess(call);
                        }
                    }
                    MDC.remove(CommonParameter.method);
                    access.logRequest();
                }
            }
        } catch (Throwable t) {
            logger.error("api execute error.", t);
            fatalError = true;
        } finally {
            try {
                // token 解析失败，删除 token 以及标志位
                if (apiContext.clearUserToken) {
                    HashMap<String, String> map = config.getOriginWhiteList();
                    // 删除 cookie 中的 user token
                    Cookie tk_cookie = new Cookie(apiContext.appid + CommonParameter.token, "");
                    tk_cookie.setMaxAge(0);
                    tk_cookie.setHttpOnly(true);
                    tk_cookie.setSecure(false);
                    tk_cookie.setPath("/");

                    // 删除 cookie 中的 secret user token
                    Cookie stk_cookie = new Cookie(apiContext.appid + CommonParameter.stoken, "");
                    stk_cookie.setMaxAge(0);
                    stk_cookie.setHttpOnly(true);
                    stk_cookie.setSecure(true);
                    stk_cookie.setPath("/");

                    // 删除 cookie 中的 登录标志位
                    Cookie ct_cookie = new Cookie(apiContext.appid + "_ct", "");
                    ct_cookie.setMaxAge(0);
                    ct_cookie.setHttpOnly(false);
                    ct_cookie.setSecure(false);
                    ct_cookie.setPath("/");

                    // 删除 用户信息
                    Cookie userInfo_cookie = new Cookie(apiContext.appid + "_uinfo", "");
                    userInfo_cookie.setMaxAge(0);
                    userInfo_cookie.setHttpOnly(false);
                    userInfo_cookie.setSecure(false);
                    userInfo_cookie.setPath("/");
                    if (apiContext.host != null && map.containsKey(apiContext.host)) {
                        String domain = map.get(apiContext.host);
                        tk_cookie.setDomain(domain);
                        ct_cookie.setDomain(domain);
                        userInfo_cookie.setDomain(domain);
                    }
                    response.addCookie(tk_cookie);
                    response.addCookie(stk_cookie);
                    response.addCookie(ct_cookie);
                    response.addCookie(userInfo_cookie);
                } else if (apiContext.clearExpiredUserToken) {
                    // token 过期，删除标志位，将客户端 token 标记为失效
                    HashMap<String, String> map = config.getOriginWhiteList();
                    // 删除 cookie 标志位
                    Cookie ct_cookie = new Cookie(apiContext.appid + "_ct", "");
                    ct_cookie.setMaxAge(0);
                    ct_cookie.setHttpOnly(false);
                    ct_cookie.setSecure(false);
                    ct_cookie.setPath("/");
                    if (apiContext.host != null && map.containsKey(apiContext.host)) {
                        ct_cookie.setDomain(map.get(apiContext.host));
                    }
                    response.addCookie(ct_cookie);
                }
                if (fatalError) {
                    // 错误请求
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                } else if (parseResult != ApiReturnCode.SUCCESS) {
                    // 访问被拒绝(如签名验证失败)
                    Exception e = output(parseResult, EMPTY_METHOD_CALL_ARRAY, response);
                    if (e != null) {
                        logger.error("output failed.", e);
                    }
                } else {
                    Exception e = output(ApiReturnCode.SUCCESS, apiContext.apiCalls.toArray(new ApiMethodCall[apiContext.apiCalls.size()]), response);
                    if (e != null) {
                        logger.error("output failed.", e);
                    }
                }
            } catch (Exception e) {
                logger.error("output failed.", e);
            } finally {
                apiContext.clear();
            }
        }
    }

    /**
     * 子类中可以扩展验证用户权限的方式
     */
    protected AbstractReturnCode checkAuthorization(ApiContext context, int authTarget, HttpServletRequest request) {
        CallerInfo caller = context.caller;

        if (SecurityType.isNone(authTarget)) {// 不进行权限控制
            return ApiReturnCode.SUCCESS;
        }

        if ((authTarget & caller.securityLevel) != authTarget) {
            logger.error("securityLevel missmatch. expact:" + authTarget + " actual:" + caller.securityLevel);
            return ApiReturnCode.SECURITY_LEVEL_MISMATCH;
        }
        return ApiReturnCode.SUCCESS;
    }

    /**
     * 子类中可以扩展校验子系统角色是否可以访问当前请求的接口
     */
    protected AbstractReturnCode checkSubSystemRole(ApiContext context, int authTarget, HttpServletRequest request) {
        return ApiReturnCode.SUCCESS;
    }

    protected boolean checkIntegratedSignature(ApiContext context, HttpServletRequest request) {
        return false;
    }

    /**
     * 解析调用者身份(在验证签名正确前此身份不受信任)
     */
    protected void parseCallerInfo(ApiContext context) {
        try {
            // user token存在时解析出调用者信息
            if (context.token != null) {
                if (context.token != null && context.token.length() > 0) {
                    context.caller = aesTokenHelper.parseToken(context.token);
                    if (context.caller != null && context.caller.uid != 0) {
                        MDC.put(CommonParameter.userId, String.valueOf(context.caller.uid));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parse token failed.", e);
        } finally {
            TraceInfo.setTraceInfo(
                    new TraceInfo(context.cid,
                            context.appid + "|" + context.deviceIdStr + "|" + context.versionCode + "|" + context.clientIP,
                            context.caller == null ? null : context.caller.uid + "|" + context.caller.subSystemId + "|" + context.caller.subSystemRole
                                    + "|" + context.caller.subSystemMainId)
            );
        }
    }

    private AbstractReturnCode parseMethodDependency(String nameString) {
        if (nameString != null && nameString.length() > 0) {
            // 解析多个由','拼接的api名. api名由3个部分组成 函数名@实例名:依赖函数名1@实例名/依赖函数名2@实例名  除了函数名以外的信息都可以缺省.
            String[] names = nameString.split(",");
            List<ApiMethodCall> apiCallList = apiContext.apiCalls = new ArrayList<>(names.length);
            Map<String, ApiMethodCall> apiCallMap = new HashMap<>(names.length);
            // 检测当前安全级别是否允许调用请求中的所有api
            for (int m = 0; m < names.length; m++) {
                String fullName = names[m];
                String instanceName = fullName.contains(":") ? fullName.substring(0, fullName.indexOf(":")) : fullName;
                String name = instanceName.contains("@") ? instanceName.substring(0, instanceName.indexOf("@")) : instanceName;
                ApiMethodInfo method = apiManager.getApiMethodInfo(name);
                if (method != null) {
                    // 接口返回RawString，不允许多接口同时调用
                    if (method.returnType == RawString.class) {
                        if (names.length > 1) {
                            return ApiReturnCode.ILLEGAL_MUTLI_RAWSTRING_RT;
                        }
                    }
                    // 调用接口中包含了SecurityType为Integrated的接口，不允许多接口同时调用
                    if (SecurityType.Integrated.check(method.securityLevel)) {
                        if (names.length > 1) {
                            return ApiReturnCode.ILLEGAL_MUTLI_INTEGRATED_API_ACCESS;
                        }
                    }
                    // 本接口只允许加密调用
                    if (method.encryptionOnly && !apiContext.isSSL) {
                        return ApiReturnCode.UNKNOW_ENCRYPTION_DENIED;
                    }
                    ApiMethodCall call = new ApiMethodCall(method);
                    apiCallList.add(call);
                    apiCallMap.put(instanceName, call);
                } else {
                    return ApiReturnCode.UNKNOWN_METHOD;
                }
            }

            // 遍历请求中的所有接口, 生成依赖关系, 并将有依赖项的请求加入 lv2ApiCalls
            for (int m = 0; m < names.length; m++) {
                String fullName = names[m];
                ApiMethodCall call = apiCallList.get(m);
                String[] dependentMethods = fullName.contains(":") ? fullName.substring(fullName.indexOf(":") + 1).split("/") : null;
                if (dependentMethods != null) {
                    if (apiContext.lv2ApiCalls == null) {
                        apiContext.lv2ApiCalls = new LinkedList<>();
                    }
                    apiContext.lv2ApiCalls.add(call);
                    call.dependencies = new ArrayList<>(3);
                    for (String methodName : dependentMethods) {
                        ApiMethodCall c = apiCallMap.get(methodName);
                        if (c == null) {
                            return ApiReturnCode.UNKNOWN_DEPENDENT_METHOD;
                        }
                        call.dependencies.add(c);
                        if (c.method.authenticationMethod) {
                            if (call.dependsAuthCall == null) {
                                call.dependsAuthCall = c;
                            } else {
                                throw new RuntimeException("duplicate auth dependency: " + fullName + " in " + nameString);
                            }
                        }
                    }
                }
            }

            // 遍历 lv2ApiCalls, 将其中有两层依赖关系的请求加入到 lv3ApiCalls
            if (apiContext.lv2ApiCalls != null) {
                for (ApiMethodCall call : apiContext.lv2ApiCalls) {
                    ApiMethodCall lastLv3Call = null;
                    for (ApiMethodCall c : call.dependencies) {
                        if (c.dependencies != null) {
                            if (apiContext.lv3ApiCalls == null) {
                                apiContext.lv3ApiCalls = new LinkedList<>();
                            }
                            if (lastLv3Call != call) {
                                apiContext.lv3ApiCalls.add(call);
                            }
                            lastLv3Call = call;
                            // 检测请求中的调用依赖层次是否超过三层
                            if (CompileConfig.isDebug) {
                                for (ApiMethodCall cc : c.dependencies) {
                                    if (cc.dependencies != null) {
                                        return ApiReturnCode.TOO_MANY_DEPENDENT_LEVEL;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 将 lv3ApiCalls 中的所有项从 lv2ApiCalls 中移除
            if (apiContext.lv3ApiCalls != null) {
                for (ApiMethodCall call : apiContext.lv3ApiCalls) {
                    apiContext.lv2ApiCalls.remove(call);
                }
            }

            return ApiReturnCode.SUCCESS;
        }
        return ApiReturnCode.REQUEST_PARSE_ERROR;
    }

    private AbstractReturnCode parseMethodInfo(HttpServletRequest request) {
        apiContext.isSSL = CommonConfig.getInstance().getInternalPort() == request.getLocalPort();
        String nameString = request.getParameter(CommonParameter.method);
        AbstractReturnCode code = parseMethodDependency(nameString);
        if (code != ApiReturnCode.SUCCESS) {
            return code;
        }
        List<ApiMethodCall> apiCallList = apiContext.apiCalls;
        int length = apiCallList.size();
        int callerSubSystemId = apiContext.caller == null ? 0 : apiContext.caller.subSystemId;

        for (int m = 0; m < length; m++) {
            ApiMethodCall call = apiCallList.get(m);
            ApiMethodInfo method = call.method;
            // 验证当前调用的接口与调用者身份中的子系统标识是否一致(子系统标识为0的接口可被所有用户访问)
            // 如果当前调用依赖于一个授权接口, 则不验证子系统匹配, 转而在后面验证认证结果中是否包含对该接口的授权
            // TODO 加入zk权限树检测 不再简单使用接口上的标记来做这个判断
            if (method.subSystemId > 0 && method.subSystemId != callerSubSystemId && call.dependsAuthCall == null) {
                return ApiReturnCode.SUBSYSTEM_MISMATCH;
            }
            if (length == 1) {
                call.businessId = request.getParameter(CommonParameter.businessId);
            } else {
                call.businessId = request.getParameter(m + "_" + CommonParameter.businessId);
            }
            // 解析业务参数使其对应各自业务api
            String[] parameters = new String[method.parameterInfos.length];
            apiContext.requiredSecurity = method.securityLevel.authorize(apiContext.requiredSecurity);
            for (int i = 0; i < parameters.length; i++) {
                ApiParameterInfo ap = method.parameterInfos[i];
                if (ap.isAutowired) {
                    if (ap.creator != null) {
                        parameters[i] = ap.creator.create();
                    } else {
                        switch (AutowireableParameter.valueOf(ap.name)) {
                            case appid:
                                parameters[i] = apiContext.caller != null ? String.valueOf(apiContext.caller.appid)
                                        : method.securityLevel.authorize(0) == 0 ? apiContext.appid : "-1";
                                break;
                            case deviceid:
                                parameters[i] = apiContext.caller != null ? String.valueOf(apiContext.caller.deviceId)
                                        : method.securityLevel.authorize(0) == 0 ? apiContext.deviceIdStr : "-1";
                                break;
                            case userid:
                                parameters[i] = apiContext.caller != null ? String.valueOf(apiContext.caller.uid) : "-1";
                                break;
                            case subSystemRole:
                                parameters[i] = apiContext.caller != null ? String.valueOf(apiContext.caller.subSystemRole) : "";
                                break;
                            case subSystemMainId:
                                parameters[i] = apiContext.caller != null ? String.valueOf(apiContext.caller.subSystemMainId) : "-1";
                                break;
                            case userAgent:
                                parameters[i] = apiContext.agent;
                                break;
                            case cookies:
                                if (ap.extInfos != null) {
                                    Map<String, String> map = new HashMap<String, String>(ap.extInfos.length);
                                    for (String n : ap.extInfos) {
                                        // 对于要求cookie注入的参数, 首先在表单中寻找, 因为不使用cookie的客户端会在表单中传递相关数据, 且约定表单中数据优先级高
                                        // 表单中的通用参数都为 '_' 开头
                                        String _n = n.startsWith("_") ? n : "_" + n;
                                        String v = request.getParameter(_n);
                                        if (v == null) {
                                            v = apiContext.getCookie(n);
                                        }
                                        if (v != null) {
                                            map.put(n, v);
                                        }
                                    }
                                    parameters[i] = JSON.toJSONString(map);
                                }
                                break;
                            case headers:
                                if (ap.extInfos != null) {
                                    Map<String, String> map = new HashMap<String, String>(ap.extInfos.length);
                                    for (String n : ap.extInfos) {
                                        String v = request.getHeader(n);
                                        if (v != null) {
                                            map.put(n, v);
                                        }
                                    }
                                    parameters[i] = JSON.toJSONString(map);
                                }
                                break;
                            case businessId:
                                parameters[i] = call.businessId;
                                break;
                            case postBody:
                                if (SecurityType.Integrated.check(method.securityLevel)) {
                                    String contentType = request.getHeader("Content-Type");
                                    if (contentType == null || contentType.length() == 0 || contentType
                                            .startsWith("application/x-www-form-urlencoded")) {
                                        parameters[i] = apiContext.recoverRequestBody();
                                    } else {
                                        parameters[i] = readPostBody(request);
                                    }
                                }
                                break;
                            case channel:
                                parameters[i] = request.getParameter(CommonParameter.channel);
                                break;
                            case thirdPartyId:
                                parameters[i] = apiContext.thirdPartyId;
                                break;
                            case versionCode:
                                parameters[i] = apiContext.versionCode;
                                break;
                            case referer:
                                parameters[i] = apiContext.referer;
                                break;
                            case host:
                                parameters[i] = apiContext.host;
                                break;
                            case token:
                                parameters[i] = apiContext.token;
                                break;
                            case secretToken:
                                parameters[i] = apiContext.stoken;
                                break;
                            case clientIP:
                                parameters[i] = apiContext.clientIP;
                                break;
                            case serviceInjection:
                                // Do nothing
                                break;
                        }
                    }
                } else {
                    if (length == 1) {
                        parameters[i] = request.getParameter(ap.name);
                    } else {
                        String parameterName = m + "_" + ap.name;
                        parameters[i] = request.getParameter(parameterName);
                    }
                    // 如果参数被标记为加密传输的，那么当其为必填或不为空的时候需要被解密后传送到业务端
                    if (ap.isRsaEncrypted && (ap.isRequired || parameters[i] != null)) {
                        try {
                            parameters[i] = new String(rsaDecryptHelper.decrypt(Base64Util.decode(parameters[i])), ConstField.UTF8);
                        } catch (Exception e) {
                            return ApiReturnCode.PARAMETER_DECRYPT_ERROR;
                        }
                    }
                }
                if (CompileConfig.isDebug) {
                    if (parameters[i] != null) {
                        call.message.append(ap.name).append('=').append(parameters[i].replace('\n', ' ')).append('&');
                    }
                } else {
                    if (ap.ignoreForSecurity) {
                        apiContext.ignoreParameterForSecurity(ap.name);
                    } else if (parameters[i] != null) {
                        call.message.append(ap.name).append('=').append(parameters[i].replace('\n', ' ')).append('&');
                    }
                }
            }
            call.parameters = parameters;
        }

        // 调试环境下为带有特殊标识的访问者赋予测试者身份
        if (CompileConfig.isDebug) {
            if ((apiContext.agent != null && apiContext.agent.contains(DEBUG_AGENT))) {
                if (apiContext.caller == null) {
                    apiContext.caller = CallerInfo.TESTER;
                }
                return ApiReturnCode.SUCCESS;
            }
        }

        //Integrate None Internal级别接口不具备用户身份
        if (SecurityType.requireToken(apiContext.requiredSecurity)) {
            // 默认验证 RegisteredDevice
            apiContext.requiredSecurity = SecurityType.RegisteredDevice.authorize(apiContext.requiredSecurity);
            if (apiContext.caller == null) {
                return ApiReturnCode.TOKEN_ERROR;
            }
        }

        if (SecurityType.Integrated.check(apiContext.requiredSecurity)) {
            if (apiContext.apiCalls.size() != 1) {
                return ApiReturnCode.ACCESS_DENIED;
            }
            // 签名验证，用于防止中间人攻击
            if (!checkIntegratedSignature(apiContext, request)) {
                return ApiReturnCode.SIGNATURE_ERROR;
            }
        } else if (!checkSignature(apiContext.caller, apiContext.requiredSecurity, request)) {
            return ApiReturnCode.SIGNATURE_ERROR;
        }

        code = checkAuthorization(apiContext, apiContext.requiredSecurity, request);
        if (code != ApiReturnCode.SUCCESS) {
            return code;
        }

        return checkSubSystemRole(apiContext, apiContext.requiredSecurity, request);
    }

    private Object processCall(ApiMethodCall call, String[] params) {
        if (CompileConfig.isDebug) {
            String targetDubboVersion = null;
            String targetDubboURL = null;

            // params 非空的情况下后两个参数是调试版本和调试地址信息
            if (params != null && params.length >= 2) {
                targetDubboVersion = params[params.length - 2];
                targetDubboURL = params[params.length - 1];
                String[] tmp = new String[params.length - 2];
                for (int i = 0; i < tmp.length; i++) {
                    tmp[i] = params[i];
                }
                params = tmp;
            }

            // 下面这段代码的性能不高,仅用作开发调试
            if (targetDubboURL != null || targetDubboVersion != null) {
                ApplicationConfig application = new ApplicationConfig();
                application.setName("api");
                ReferenceConfig reference = new ReferenceConfig(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
                reference.setTimeout(30000);
                reference.setApplication(application);
                if (targetDubboURL != null) {
                    reference.setUrl(targetDubboURL);
                } else {
                    // 连接注册中心配置
                    String[] addressArray = ZkAddress.split(" ");
                    List<RegistryConfig> registryConfigList = new LinkedList<RegistryConfig>();
                    for (String zkAddress : addressArray) {
                        RegistryConfig registry = new RegistryConfig();
                        registry.setAddress(zkAddress);
                        registry.setProtocol("dubbo");
                        registryConfigList.add(registry);
                    }
                    reference.setRegistries(registryConfigList);// 多个注册中心可以用setRegistries()
                }
                reference.setInterface(call.method.dubboInterface);
                reference.setRetries(0);
                reference.setAsync(CommonConfig.getInstance().getDubboAsync());
                reference.setVersion(targetDubboVersion);
                Object service = null;
                try {
                    service = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
                } catch (Exception e) {
                    logger.error("get service failed.", e);
                }
                HttpApiExecutor executor = HttpApiProvider.getApiExecutor(call.method.methodName, call.method);
                executor.setInstance(service);// 重设服务实例

                // 当接口为 mock 实现时, 将指向客户端指定位置的 dubbo service 注入给 mock 实现
                if (call.method.mocked) {
                    if (service != null) {
                        ((MockApiImplementation)call.method.serviceInstance).$setProxy(service);
                    }
                } else {
                    return call.method.wrapper.wrap(executor.execute(params));
                }
            }
        }

        // TODO: get role/permission map from zk
        return apiManager.processRequest(call.method.methodName, params);
    }

    private void setResponseHeader(HttpServletRequest request, HttpServletResponse response) {
        //解决H5跨域问题
        {
            String origin = request.getHeader("Origin");
            if (origin != null && CommonConfig.getInstance().getOriginWhiteList().containsKey(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.addHeader("Access-Control-Allow-Method", "POST, GET");
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
        }

        // 设置response 的content type
        {
            switch (apiContext.format) {
                case JSON:
                    if (apiContext.jsonpCallback == null) {
                        response.setContentType(CONTENT_TYPE_JSON);
                    } else {
                        response.setContentType(CONTENT_TYPE_JAVASCRIPT);
                    }
                    break;
                case XML:
                    response.setContentType(CONTENT_TYPE_XML);
                    break;
                case PAILNTEXT:
                    response.setContentType(CONTENT_TYPE_PLAINTEXT);
                    break;
            }
        }

        {
            if (apiContext.deviceIdStr != null && apiContext.deviceIdStr.length() > 0) {
                try {
                    long did = Long.parseLong(apiContext.deviceIdStr);
                    apiContext.deviceId = did;

                    // user token 解析失败，删除 cookie 中的 user token
                    if (apiContext.token != null && apiContext.caller == null) {
                        apiContext.clearUserToken = true;
                    }
                } catch (Exception e) {
                    logger.error("deviceId error " + apiContext.deviceIdStr, e);
                    setDeviceIDinCookie(response);
                }
            } else {
                setDeviceIDinCookie(response);
            }
        }
    }

    private void setDeviceIDinCookie(HttpServletResponse response) {
        if (apiContext.appid == null) {
            return;
        }
        apiContext.deviceId = -(1_000_000_000_000_000L + ((long)(Math.random() * 9_000_000_000_000_000L)));
        apiContext.deviceIdStr = String.valueOf(apiContext.deviceId);
        MDC.put(CommonParameter.deviceId, apiContext.deviceIdStr);
        HashMap<String, String> map = CommonConfig.getInstance().getOriginWhiteList();
        Cookie deviceId_cookie = new Cookie(CommonParameter.cookieDeviceId, apiContext.deviceIdStr);
        deviceId_cookie.setMaxAge(Integer.MAX_VALUE);
        deviceId_cookie.setSecure(false);
        deviceId_cookie.setPath("/");
        if (apiContext.host != null && map.containsKey(apiContext.host)) {
            deviceId_cookie.setDomain(map.get(apiContext.host));
        }
        response.addCookie(deviceId_cookie);
    }

    @SuppressWarnings("unchecked")
    private void serializeCallResult(ApiMethodCall call) throws IOException {
        int oldSize = apiContext.outputStream.size();
        try {
            switch (apiContext.format) {
                case XML:
                    if (call.result == null) {
                        if (call.method.returnType != RawString.class) {
                            apiContext.outputStream.write(ConstField.XML_EMPTY);
                        }
                    } else {
                        ((Serializer<Object>)call.method.serializer).toXml(call.result, apiContext.outputStream, true);
                    }
                    break;
                case JSON:
                    if (apiContext.serializeCount > 0) {
                        apiContext.outputStream.write(ConstField.JSON_SPLIT);
                    }
                    if (call.result == null) {
                        if (call.method.returnType != RawString.class) {
                            apiContext.outputStream.write(ConstField.JSON_EMPTY);
                        }
                    } else {
                        ((Serializer<Object>)call.method.serializer).toJson(call.result, apiContext.outputStream, true);
                    }
                    break;
            }
            call.resultLen = apiContext.outputStream.size() - oldSize;
        } catch (Exception e) {
            //序列化失败,重置输出流（Tips：writeTo函数实现 out.write(this.buffer, 0, this.count)，故重置index即完成重置）
            apiContext.outputStream.setWriteIndex(oldSize);
            //回写空数据节点
            switch (apiContext.format) {
                case XML:
                    apiContext.outputStream.write(ConstField.XML_EMPTY);
                    break;
                case JSON:
                    if (apiContext.serializeCount > 0) {
                        apiContext.outputStream.write(ConstField.JSON_SPLIT);
                    }
                    apiContext.outputStream.write(ConstField.JSON_EMPTY);
                    break;
            }
            //设置序列化异常错误码
            call.resultLen = 0;
            call.replaceReturnCode(ApiReturnCode.SERIALIZE_FAILED);
            logger.error("serialize object failed.", e);
        } finally {
            apiContext.serializeCount++;
        }
    }

    /**
     * 解析参数以及cookie中的信息，这里返回任何预定义的错误信息
     */
    private void parseCommonParameter(HttpServletRequest request, HttpServletResponse response) {
        // 解析通用参数
        {
            apiContext.agent = request.getHeader(USER_AGENT);
            apiContext.referer = request.getHeader(REFERER);
            apiContext.clientIP = MiscUtil.getClientIP(request);
            apiContext.cid = request.getParameter(CommonParameter.callId);
            if (apiContext.cid != null && apiContext.cid.length() > 32) {
                apiContext.cid = apiContext.cid.substring(0, 32);
            }
            if (apiContext.cid == null) {
                apiContext.cid = SERVER_ADDRESS + CommonConfig.getInstance().getServerAddress()
                        + SPLIT + THREADID + Thread.currentThread().getId()
                        + SPLIT + REQ_TAG + apiContext.startTime;
            }
            apiContext.host = request.getHeader("host");
            apiContext.versionCode = request.getParameter(CommonParameter.versionCode);
            apiContext.deviceIdStr = request.getParameter(CommonParameter.deviceId);
            apiContext.uid = request.getParameter(CommonParameter.userId);
            String jsonpCallback = request.getParameter(CommonParameter.jsonpCallback);
            apiContext.token = request.getParameter(CommonParameter.token);
            if (jsonpCallback != null) {
                if (apiContext.callbackRegex.matcher(jsonpCallback).matches()) {
                    apiContext.jsonpCallback = jsonpCallback.getBytes(ConstField.UTF8);
                } else {
                    logger.error("unsupported callback name : " + jsonpCallback);
                }
            }
            MDC.put(CommonParameter.callId, apiContext.cid);
            MDC.put("_cip", apiContext.clientIP);
            if (apiContext.deviceIdStr != null) {
                MDC.put(CommonParameter.deviceId, apiContext.deviceIdStr);
            }
        }

        //应用编号,
        {
            apiContext.appid = request.getParameter(CommonParameter.applicationId);
            MDC.put(CommonParameter.applicationId, apiContext.appid);
        }

        {
            // 优先使用 url 中的 userToken 和 deviceId
            Cookie[] cs = request.getCookies();
            if (cs != null) {
                String tokenName = apiContext.appid + CommonParameter.token;
                String stokenName = apiContext.appid + CommonParameter.stoken;
                for (Cookie c : cs) {
                    if (tokenName.equals(c.getName())) {
                        try {
                            if (apiContext.token == null && c.getValue() != null && !c.getValue().isEmpty()) {
                                apiContext.token = URLDecoder.decode(c.getValue(), "utf-8");
                            }
                        } catch (Exception e) {
                            logger.error("token in cookie error " + c.getValue(), e);
                            apiContext.clearUserToken = true;
                        }
                    } else if (stokenName.equals(c.getName())) {
                        try {
                            if (apiContext.stoken == null && c.getValue() != null && !c.getValue().isEmpty()) {
                                apiContext.stoken = URLDecoder.decode(c.getValue(), "utf-8");
                            }
                        } catch (Exception e) {
                            logger.error("stoken in cookie error " + c.getValue(), e);
                            apiContext.clearUserToken = true;
                        }
                    } else if (CommonParameter.cookieDeviceId.equals(c.getName())) {
                        if (apiContext.deviceIdStr == null) {
                            apiContext.deviceIdStr = c.getValue();
                            MDC.put(CommonParameter.deviceId, apiContext.deviceIdStr);
                        }
                    } else {
                        apiContext.addCookie(c.getName(), c.getValue());
                    }
                }
            }
        }

        //集成第三方的编号，这个编号没有太高的安全性要求，采用明文方式传输即可
        {
            apiContext.thirdPartyId = request.getParameter(CommonParameter.thirdPartyId);
        }

        // 确定返回的错误提示信息语言
        {
            apiContext.location = request.getParameter(CommonParameter.location);
        }

        // 构造请求字符串用于日志记录
        {
            parseRequestInfo(request);
        }

        // 确定返回值的序列化类型
        {
            parseFormatType(request);
        }

        // 解析调用者身份(在验证签名正确前此身份不受信任)
        {
            parseCallerInfo(apiContext);
        }
    }

    /**
     * 构造请求字符串用于日志记录
     */
    private void parseRequestInfo(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        apiContext.requestInfo = new HashMap<String, String>();
        for (String key : map.keySet()) {
            String[] values = map.get(key);
            if (values.length > 1) {
                logger.error("parameter " + key + " has " + values.length + " values " + StringUtils.join(values, "|||"));
            }
            apiContext.requestInfo.put(key, (values == null || values.length == 0) ? "" : values[0]);
        }
    }

    /**
     * 确定返回值的序列化类型
     */
    private void parseFormatType(HttpServletRequest request) {
        String format = request.getParameter(CommonParameter.format);
        if (format != null && format.length() > 0) {
            if (format.equals(FORMAT_XML)) {
                apiContext.format = SerializeType.XML;
            } else if (format.equals(FORMAT_JSON)) {
                apiContext.format = SerializeType.JSON;
            } else if (format.equals(FORMAT_PLAINTEXT)) {
                apiContext.format = SerializeType.PAILNTEXT;
            } else {
                apiContext.format = SerializeType.JSON;
            }
        } else {
            apiContext.format = SerializeType.JSON;
        }
    }

    private void executeAllApiCall(List<ApiMethodCall> calls, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommonConfig config = CommonConfig.getInstance();
        Future<?>[] futures = new Future[calls.size()];
        RpcContext rpcContext = RpcContext.getContext();
        for (int count = 0; count < futures.length; count++) {
            ApiMethodCall call = calls.get(count);
            apiContext.currentCall = call;
            MDC.put(CommonParameter.method, call.method.methodName);
            call.startTime = System.currentTimeMillis();
            // 填装服务端隐式传递的参数
            if (call.dependencies != null) {
                for (int i = 0; i < call.parameters.length; i++) {
                    ApiParameterInfo p = call.method.parameterInfos[i];
                    // 处理隐式参数注入
                    if (p.injectable != null) {
                        String key = p.injectable.getName();
                        String httpParam = call.parameters[i];
                        ServiceInjectable.InjectionData injectionData = null;
                        // 当参数没有注明 autowired 的时候, 合并客户端请求的参数值和服务端隐式传入的参数
                        if (!p.isAutowired && httpParam != null && httpParam.length() > 0) {
                            try {
                                injectionData = p.injectable.parseDataFromHttpParam(httpParam);
                            } catch (Exception e) {
                                throw new RuntimeException("service injection failed. 参数解析失败: " + httpParam, e);
                            }
                        }
                        // 合并该调用所有依赖项中的 key 键对应的值
                        for (ApiMethodCall dependency : call.dependencies) {
                            if (dependency.exportParams != null && dependency.exportParams.containsKey(key)) {
                                String notificationData = null;
                                try {
                                    notificationData = dependency.exportParams.get(key);
                                    if (notificationData != null) {
                                        ServiceInjectable.InjectionData data = JSON.parseObject(notificationData, p.injectable.getDataType());

                                        if (injectionData == null) {
                                            injectionData = data;
                                        } else {
                                            injectionData.batchMerge(data);
                                        }
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException("service injection failed. notification 解析失败: " + notificationData
                                            + " from method:" + dependency.method.methodName, e);
                                }
                            }
                        }
                        if (injectionData != null) {
                            Object data = injectionData.getValue();
                            if (data != null) {
                                if (data instanceof String) {
                                    call.parameters[i] = (String)data;
                                } else {
                                    call.parameters[i] = JSON.toJSONString(data);
                                }
                                call.message.append(injectionData.getName()).append('=').append(call.parameters[i].replace('\n', ' ')).append('&');
                            }
                        }
                    } else if (p.isAutowired && AutowireableParameter.userid.equals(p.name)) {
                        // 将授权接口的授权结果注入给当前接口
                        if (call.dependsAuthCall != null && call.dependsAuthCall == apiContext.authCall) {
                            AuthenticationResult authResult = (AuthenticationResult)apiContext.authCall.result;
                            for (String authApi : authResult.apis) {
                                if (call.method.methodName.equals(authApi)) {
                                    call.parameters[i] = String.valueOf(authResult.authorizedUserId);
                                }
                            }
                        }
                    }
                }
            }
            // dubbo 在调用结束后不会清除 Future 为了避免拿到之前接口对应的 Future 在这里统一清除
            rpcContext.setFuture(null);
            // 当前接口依赖的授权调用如果失败则将当前接口标记为调用失败
            if (call.dependsAuthCall != null && (call.dependsAuthCall != apiContext.authCall || apiContext.authResult == null)) {
                call.setReturnCode(ApiReturnCode.SUBSYSTEM_AUTHENTICATION_FAILED);
                call.costTime = (int)(System.currentTimeMillis() - call.startTime);
            } else {
                executeApiCall(rpcContext, call, request, response, null);
                // 即使打开异步, 该接口还可能被 mock 或被短路
                if (config.getDubboAsync()) {
                    // 如果配置为异步执行时，该接口恰好短路结果或mock, 此处获得的future为null
                    futures[count] = rpcContext.getFuture();
                } else {
                    call.costTime = (int)(System.currentTimeMillis() - call.startTime);
                }
            }
        }
        for (int count = 0; count < futures.length; count++) {
            ApiMethodCall call = calls.get(count);
            ApiMethodInfo info = call.method;
            MDC.put(CommonParameter.method, info.methodName);
            // 等待异步执行返回
            if (futures[count] != null) {
                executeApiCall(rpcContext, call, request, response, futures[count]);
                // TODO: 通过 future 回调来设置costtime
                call.costTime = (int)(System.currentTimeMillis() - call.startTime);
            }
            int display = call.getReturnCode();
            if (display > 0) {
                if (info.errors == null) {
                    call.replaceReturnCode(ApiReturnCode.UNKNOWN_ERROR);
                } else {
                    // 异常编码过滤，保证接口只返回其声明过的异常编码给客户端
                    if (Arrays.binarySearch(info.errors, display) < 0) {
                        // 查询当前code是否在内部映射表
                        int index = info.innerCodeMap == null ? -1 : info.innerCodeMap.get(display, -1);
                        if (index == -1) {
                            call.replaceReturnCode(ApiReturnCode.UNKNOWN_ERROR);
                        } else {
                            call.replaceReturnCode(info.errorCodes[index]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行具体的api接口调用, 本接口可能被执行两次，不要在其中加入任何状态相关的操作
     */
    private void executeApiCall(RpcContext context, ApiMethodCall call, HttpServletRequest request, HttpServletResponse response, Future future) {
        try {
            ApiMethodInfo method = call.method;
            // 当接口声明了静态 mock 返回值或被标记为短路时
            if (method.staticMockValue != null) {
                call.result = method.staticMockValue;
            } else {
                if (future != null) {
                    FutureAdapter<?> fa = (FutureAdapter<?>)future;
                    final ResponseCallback callback = fa.getFuture().getCallback();
                    // 异步调用会导致dubbo filter处理返回值的部分失效(因为异步返回并触发filter的时候并没有返回任何值),
                    // 因此在这里进行补偿操作。
                    // TODO: 回滚对 dubbo future callback 的修改
                    fa.getFuture().setCallback(new NotificationManager(callback));
                    if (fa.getFuture().isDone()) {
                        NotificationManager.saveNotifications(fa.getFuture().get());
                    }
                    call.result = method.wrapper.wrap(future.get());
                } else {
                    String[] parameters = call.parameters;
                    // 根据客户端在Header中设定的目标dubbo服务的版本号或者url，绕过注册中心调用对应的dubbo服务，仅在DEBUG模式下允许使用
                    if (CompileConfig.isDebug) {
                        parameters = new String[call.parameters == null ? 2 : call.parameters.length + 2];
                        if (call.parameters != null) {
                            for (int i = 0; i < call.parameters.length; i++) {
                                parameters[i] = call.parameters[i];
                            }
                        }
                        parameters[call.parameters.length] = request.getHeader(DEBUG_DUBBOVERSION);
                        parameters[call.parameters.length + 1] = request.getHeader(DEBUG_DUBBOSERVICE_URL);
                    }
                    if (method.type == ApiMethodInfo.Type.DUBBO) {
                        call.result = processCall(call, parameters);
                    } else {
                        Object[] objs = new Object[method.parameterInfos.length];
                        int i = 0;
                        for (ApiMethodCall c : call.dependencies) {
                            objs[i] = c.result;
                            i++;
                        }
                        call.result = method.wrapper.wrap(apiManager.processMix(method.methodName, objs));
                        return;
                    }

                    // 若当前是异步调用 则直接返回
                    if (context.getFuture() != null) {
                        return;
                    }
                }

                if (method.authenticationMethod) {
                    if (apiContext.authCall == null) {
                        // 组合接口数不能超过16
                        if (CompileConfig.isDebug) {
                            AuthenticationResult ar = (AuthenticationResult)call.result;
                            if (ar.apis == null || ar.apis.length > 16) {
                                throw new RuntimeException("authentication api list is empty or more than 10");
                            }
                        }
                        apiContext.authCall = call;
                        apiContext.authResult = (AuthenticationResult)call.result;
                        call.result = null;
                    } else {
                        // 不支持一次http调用中包含多个子系统授权调用
                    }
                }
            }
            //dubbo接口能够获取到RpcContext中的notification,非dubbo的接口errorCode不是通过RpcContext传递的。
            Map<String, String> notifications = NotificationManager.getNotifications();
            NotificationManager.clear();
            if (notifications != null && notifications.size() > 0) {
                for (Map.Entry<String, String> entry : notifications.entrySet()) {
                    String value = entry.getValue();
                    if (ConstField.SET_COOKIE_STOKEN.equals(entry.getKey())) {
                        // do nothing
                    } else if (ConstField.SET_COOKIE_TOKEN.equals(entry.getKey())) {
                        HashMap<String, String> map = CommonConfig.getInstance().getOriginWhiteList();
                        if (value != null && value.length() > 0) {
                            Cookie tk_cookie = new Cookie(apiContext.appid + CommonParameter.token, URLEncoder.encode(value, "utf-8"));
                            tk_cookie.setMaxAge(-1);
                            tk_cookie.setHttpOnly(true);
                            tk_cookie.setSecure(false);
                            tk_cookie.setPath("/");

                            String stk = notifications.get(ConstField.SET_COOKIE_STOKEN);
                            int duration = -1;
                            try {
                                int index = stk.lastIndexOf("|");
                                if (index > 0) {
                                    duration = Integer.valueOf(stk.substring(index + 1));
                                    stk = stk.substring(0, index);
                                }

                            } catch (Exception e) {
                                logger.error("parse stk expire time error." + stk);
                            }
                            Cookie stk_cookie = new Cookie(apiContext.appid + CommonParameter.stoken,
                                    notifications.get(ConstField.SET_COOKIE_STOKEN) == null ? "" : URLEncoder.encode(
                                            stk, "utf-8"));
                            stk_cookie.setMaxAge(duration);
                            stk_cookie.setHttpOnly(true);
                            stk_cookie.setSecure(true);
                            stk_cookie.setPath("/");

                            // 用于提示客户端当前token是否存在
                            Cookie ct_cookie = new Cookie(apiContext.appid + "_ct", "1");
                            ct_cookie.setMaxAge(-1);
                            ct_cookie.setHttpOnly(false);
                            ct_cookie.setSecure(false);
                            ct_cookie.setPath("/");

                            String domain = map.get(apiContext.host);
                            if (CompileConfig.isDebug) {
                                logger.info(
                                        "host:" + apiContext.host + " in map:" + map.containsKey(apiContext.host) + " domain:" + domain);
                            }

                            if (apiContext.host != null && map.containsKey(apiContext.host)) {
                                tk_cookie.setDomain(domain);
                                stk_cookie.setDomain(domain);
                                ct_cookie.setDomain(domain);
                            }
                            response.addCookie(tk_cookie);
                            response.addCookie(stk_cookie);
                            response.addCookie(ct_cookie);
                            apiContext.clearUserToken = false; // user token will be override.
                        } else { // 删除cookie
                            apiContext.clearUserToken = true;
                        }
                    } else if (ConstField.SET_COOKIE_USER_INFO.equals(entry.getKey())) {
                        HashMap<String, String> map = CommonConfig.getInstance().getOriginWhiteList();
                        if (value != null) {
                            Cookie userInfo_cookie = new Cookie(apiContext.appid + "_uinfo", URLEncoder.encode(value, "utf-8"));
                            userInfo_cookie.setMaxAge(Integer.MAX_VALUE);
                            userInfo_cookie.setHttpOnly(false);
                            userInfo_cookie.setSecure(false);
                            userInfo_cookie.setPath("/");

                            if (apiContext.host != null && map.containsKey(apiContext.host)) {
                                userInfo_cookie.setDomain(map.get(apiContext.host));
                            }
                            response.addCookie(userInfo_cookie);
                        }
                    } else if (ConstField.SERVICE_LOG.equals(entry.getKey())) {
                        call.serviceLog = value;
                    } else if (ConstField.REDIRECT_TO.equals(entry.getKey())) {
                        response.sendRedirect(entry.getValue());
                    } else if (entry.getKey().startsWith(ConstField.SERVICE_PARAM_EXPORT_PREFIX)) {
                        if (call.exportParams == null) {
                            call.exportParams = new HashMap<>();
                        }
                        // TODO:check export name
                        call.exportParams.put(entry.getKey().substring(ConstField.SERVICE_PARAM_EXPORT_PREFIX.length()), entry.getValue());
                    } else {
                        apiContext.addNotification(new KeyValuePair(entry.getKey(), JSONARRAY_PREFIX + value + JSONARRAY_SURFIX));
                    }
                }
            }
            call.setReturnCode(ApiReturnCode.SUCCESS);
        } catch (ReturnCodeException rce) {
            call.setReturnCode(rce.getCode());
            if (rce.getCode() == ApiReturnCode.PARAMETER_ERROR || rce.getCode() == ApiReturnCode.ROLE_DENIED) {
                logger.error("servlet catch an api error. " + rce.getMessage());
            } else {
                logger.error("servlet catch an api error.", rce);
            }
        } catch (Throwable t) {
            if (t instanceof ServiceException) {
                ServiceException se = (ServiceException)t;
                logger.error("service exception. code:" + se.getCode() + " msg:" + se.getMsg());
                call.setReturnCode(se.getCode(), se.getDisplayCode(), se.getCode() == se.getDisplayCode() ? se.getMsg() : se.getDescription());
            } else if (t.getCause() instanceof ServiceException) {
                ServiceException se = (ServiceException)t.getCause();
                logger.error("inner service exception. code:" + se.getCode() + " msg:" + se.getMsg());
                call.setReturnCode(se.getCode(), se.getDisplayCode(), se.getCode() == se.getDisplayCode() ? se.getMsg() : se.getDescription());
            } else if (t.getCause() instanceof com.alibaba.dubbo.remoting.TimeoutException) {
                logger.error("dubbo timeout.", t);
                call.setReturnCode(ApiReturnCode.DUBBO_SERVICE_TIMEOUT_ERROR);
            } else if (t.getCause() instanceof com.alibaba.dubbo.remoting.RemotingException) {
                logger.error("dubbo service exception.", t);
                call.setReturnCode(ApiReturnCode.DUBBO_SERVICE_ERROR);
            } else if (t instanceof com.alibaba.dubbo.rpc.RpcException) {
                logger.error("dubbo exception.", t);
                call.setReturnCode(ApiReturnCode.DUBBO_SERVICE_NOTFOUND_ERROR);
            } else {
                logger.error("internal error.", t);
                call.setReturnCode(ApiReturnCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private String readPostBody(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            if (CompileConfig.isDebug) {
                logger.error("read post body failed.", e);
            }
        }
        return sb.toString();
    }

    /**
     * 输出返回到调用端
     */
    private Exception output(AbstractReturnCode code, ApiMethodCall[] calls, HttpServletResponse response) {
        Exception outputException = null;

        try {
            if (calls.length == 1 && calls[0].method.returnType == RawString.class) {// rawString的处理，将dubbo service返回的结果直接输出
                OutputStream output = response.getOutputStream();
                if (code == ApiReturnCode.SUCCESS && calls[0].getReturnCode() == ApiReturnCode.SUCCESS.getCode()) {
                    apiContext.outputStream.writeTo(output);
                } else if (code != ApiReturnCode.SUCCESS) {
                    output.write(code.getName().getBytes(ConstField.UTF8));
                } else {
                    output.write(calls[0].getReturnMessage().getBytes(ConstField.UTF8));
                }
            } else {
                Response apiResponse = new Response();
                apiResponse.code = code.getDisplay().getCode();
                apiResponse.stateList = new ArrayList<CallState>(calls.length);
                if (apiContext.cid != null) {
                    apiResponse.cid = apiContext.cid;
                }
                for (ApiMethodCall call : calls) {
                    CallState state = new CallState();
                    state.code = call.getReturnCode();
                    state.msg = call.getReturnMessage();
                    if (CompileConfig.isDebug) {
                        if (call.getReturnCode() != call.getOriginCode()) {
                            state.msg = state.msg + ":" + call.getOriginCode();// debug模式将实际errorcode外露到msg中
                        }
                    }
                    // TODO: get message i10n
                    state.length = call.resultLen;
                    apiResponse.stateList.add(state);
                }
                apiResponse.systime = System.currentTimeMillis();
                apiResponse.notificationList = apiContext.getNotifications();

                OutputStream output = response.getOutputStream();

                switch (apiContext.format) {
                    case XML:
                        output.write(ConstField.XML_START);
                        apiResponseSerializer.toXml(apiResponse, output, true);
                        apiContext.outputStream.writeTo(output);
                        output.write(ConstField.XML_END);
                        break;
                    case JSON:
                        if (apiContext.jsonpCallback != null) {
                            output.write(apiContext.jsonpCallback);
                            output.write(ConstField.JSONP_START);
                        }
                        output.write(ConstField.JSON_START);
                        apiResponseSerializer.toJson(apiResponse, output, true);
                        output.write(ConstField.JSON_CONTENT);
                        apiContext.outputStream.writeTo(output);
                        output.write(ConstField.JSON_END);
                        if (apiContext.jsonpCallback != null) {
                            output.write(ConstField.JSONP_END);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            outputException = e;
        }
        return outputException;
    }

    /**
     * 签名验证，在debug编译的环境中允许使用特定user agent跳过签名验证
     */
    public static boolean checkSignature(CallerInfo caller, int securityLevel, HttpServletRequest request) {
        // 拼装被签名参数列表
        StringBuilder sb = getSortedParameters(request);

        // 验证签名
        String sig = request.getParameter(CommonParameter.signature);
        if (sig != null && sig.length() > 0) {
            // 安全级别为None的接口仅进行静态秘钥签名验证,sha1,md5
            String sm = request.getParameter(CommonParameter.signatureMethod);
            SignatureAlgorithm sa = null;
            if (sm == null) {
                sa = SignatureAlgorithm.SHA1;
            } else {
                sa = SignatureAlgorithm.valueOf(sm.toUpperCase());
            }
            if (SecurityType.isNone(securityLevel)) {
                String staticSignPwd = CommonConfig.getInstance().getStaticSignPwd();
                switch (sa) {
                    case MD5: {
                        byte[] expect = HexStringUtil.toByteArray(sig);
                        byte[] actual = Md5Util.compute(sb.append(staticSignPwd).toString().getBytes(ConstField.UTF8));
                        return Arrays.equals(expect, actual);
                    }
                    case SHA1: {
                        byte[] expect = Base64Util.decode(sig);
                        byte[] actual = SHAUtil.computeSHA1(sb.append(staticSignPwd).toString().getBytes(ConstField.UTF8));
                        return Arrays.equals(expect, actual);
                    }
                }
            } else if (caller != null) {// 所有有安全验证需求的接口需要检测动态签名，
                switch (sa) {
                    case MD5: {
                        sb.append(HexStringUtil.toHexString(caller.key));
                        return Arrays.equals(HexStringUtil.toByteArray(sig), Md5Util.compute(sb.toString().getBytes(ConstField.UTF8)));
                    }
                    case SHA1: {
                        sb.append(HexStringUtil.toHexString(caller.key));
                        return Arrays.equals(Base64Util.decode(sig), SHAUtil.computeSHA1(sb.toString().getBytes(ConstField.UTF8)));
                    }
                    case RSA: {
                        return RsaHelper.verify(Base64Util.decode(sig), sb.toString().getBytes(ConstField.UTF8), caller.key);
                    }
                    case ECC: {
                        return EccHelper.verify(Base64Util.decode(sig), sb.toString().getBytes(ConstField.UTF8), caller.key);
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static StringBuilder getSortedParameters(HttpServletRequest request) {
        // 拼装被签名参数列表
        StringBuilder sb = new StringBuilder(128);
        {
            List<String> list = new ArrayList<String>(10);
            Enumeration<String> keys = request.getParameterNames();
            while (keys.hasMoreElements()) {
                list.add(keys.nextElement());
            }
            // 参数排序
            String[] array = list.toArray(new String[list.size()]);
            if (array.length > 0) {
                Arrays.sort(array, StringUtil.StringComparator);
                for (String key : array) {
                    if (CommonParameter.signature.equals(key)) {
                        continue;
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(request.getParameter(key));
                }
            }
        }
        return sb;
    }
}