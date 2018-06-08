package net.pocrd.core;

import com.alibaba.fastjson.JSON;
import net.pocrd.annotation.*;
import net.pocrd.define.*;
import net.pocrd.entity.*;
import net.pocrd.responseEntity.*;
import net.pocrd.util.*;
import net.pocrd.util.TypeCheckUtil.DescriptionAnnotationChecker;
import net.pocrd.util.TypeCheckUtil.PublicFieldChecker;
import net.pocrd.util.TypeCheckUtil.SerializableImplChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口管理器，用于管理api的注册过程 目前使用HashMap作为存储容器是因为当前的ApiManager在应用启动时进行静态初始化，能保证线程 安全的进行数据插入操作。在这之后都是对HashMap的多线程只读访问。
 *
 * @author rendong
 */
public final class ApiManager {
    private static final Logger                       logger      = LoggerFactory.getLogger(ApiManager.class);
    private              Map<String, HttpApiExecutor> nameToApi   = new ConcurrentHashMap<String, HttpApiExecutor>();
    private              Map<String, ApiMixer>        nameToMixer = new ConcurrentHashMap<String, ApiMixer>();
    private              Map<String, ApiMethodInfo>   apiInfos    = new ConcurrentHashMap<String, ApiMethodInfo>();
    private static final String                       UNDER_SCORE = "_";

    public ApiManager() {
    }

    public void register(List<ApiMethodInfo> apis, Object serviceInstance) {
        register("", apis, serviceInstance);
    }

    /**
     * jarfilename用来定位是哪个jar包出的问题
     */
    public void register(final String jarfilename, List<ApiMethodInfo> apis, Object serviceInstance) {
        if (apis == null) {
            return;
        }
        try {
            for (ApiMethodInfo api : apis) {
                if (CompileConfig.isDebug) {
                    if (apiInfos.containsKey(api.methodName)) {
                        throw new RuntimeException("duplicate definision for " + api.methodName);
                    }
                    if (api.state == ApiOpenState.DOCUMENT) {//只需要生成文档的api，不创建代理
                        apiInfos.put(api.methodName, api);
                    }
                }
                api.serviceInstance = serviceInstance;
                if (api.state == ApiOpenState.OPEN || api.state == ApiOpenState.DEPRECATED) {
                    apiInfos.put(api.methodName, api);
                    switch (api.type) {
                        case DUBBO: nameToApi.put(api.methodName, HttpApiProvider.getApiExecutor(api.methodName, api)); break;
                        case MIXER: nameToMixer.put(api.methodName, HttpMixerProvider.getMixerExecutor(api.methodName, api)); break;
                        default: throw new RuntimeException("Unsupported api type : " + api.type);
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("register api failed.jar file name:" + jarfilename, t);
            t.printStackTrace();
        }
    }

    /**
     * 获取某接口信息
     */
    public ApiMethodInfo getApiMethodInfo(String name) {
        ApiMethodInfo methodInfo = apiInfos.get(name);
        if (CompileConfig.isDebug) {
            if (methodInfo != null) {//DOCUMENT 不对外暴露
                if (methodInfo.state == ApiOpenState.DOCUMENT || methodInfo.securityLevel == SecurityType.Document) {
                    return null;
                }
            }
        }
        return methodInfo;
    }

    /**
     * 获取已注册接口
     */
    public ApiMethodInfo[] getApiMethodInfos() {
        return apiInfos.values().toArray(new ApiMethodInfo[apiInfos.size()]);
    }

    /**
     * 处理Api请求
     */
    public final Object processRequest(String name, String[] parameters) {
        return apiInfos.get(name).wrapper.wrap(nameToApi.get(name).execute(parameters));
    }

    public final Object processMix(String name, Object[] parameters) {
        return apiInfos.get(name).wrapper.wrap(nameToMixer.get(name).execute(parameters));
    }

    /**
     * 是否是常量
     */
    public static boolean isConstField(Field field) {
        int efm = field.getModifiers();
        if (Modifier.isPublic(efm) && Modifier.isStatic(efm) && Modifier.isFinal(efm)) {
            return true;
        }
        return false;
    }

    public static ApiMethodInfo parseMixer(Class<?> clazz) {
        HttpDataMixer mixer = clazz.getAnnotation(HttpDataMixer.class);
        if (mixer == null) {
            return null;
        }
        Method mixMethod = null;
        for (Method m : clazz.getDeclaredMethods()) {
            if ("mix".equals(m.getName())) {
                if (mixMethod == null) {
                    mixMethod = m;
                } else {
                    logger.warn("More than one mix method found in " + clazz.getName());
                    return null;
                }
            }
        }
        if (mixMethod == null) {
            logger.warn("None mix method found in " + clazz.getName());
            return null;
        }
        if (!Modifier.isPublic(mixMethod.getModifiers()) || !Modifier.isStatic(mixMethod.getModifiers())) {
            logger.warn("Mix method should be 'public' & 'static'. found in " + clazz.getName());
            return null;
        }
        if (mixMethod.getParameterCount() == 0) {
            logger.warn("Atleast one mix parameter" + clazz.getName());
            return null;
        }
        ApiMethodInfo info = new ApiMethodInfo();
        info.type = ApiMethodInfo.Type.MIXER;
        info.methodName = mixer.name();
        info.description = mixer.desc();
        info.owner = mixer.owner();
        info.returnType = mixMethod.getReturnType();
        info.proxyMethodInfo = mixMethod;

        parseReturnType(info, mixMethod, clazz);//返回结果解析,设置apiInfo.seriliazer,apiInfo.returnType, apiInfo.actuallyGenericType
        //递归检查返回结果类型
        TypeCheckUtil.recursiveCheckReturnType(clazz.getName(), info.returnType, info.actuallyGenericReturnType,
                new SerializableImplChecker(), new DescriptionAnnotationChecker(),
                new PublicFieldChecker());

        Parameter[] params = mixMethod.getParameters();
        ApiParameterInfo[] pInfos = new ApiParameterInfo[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            ApiParameterInfo pInfo = new ApiParameterInfo();
            pInfo.type = p.getType();
            pInfo.name = p.getName();
            pInfos[i] = pInfo;
        }
        info.parameterInfos = pInfos;
        return info;
    }

    public static List<ApiMethodInfo> parseApi(Class<?> clazz) {
        ApiGroup groupAnnotation = clazz.getAnnotation(ApiGroup.class);
        if (groupAnnotation == null) {
            return null;
        }
        try {
            SubSystem subSystem = clazz.getAnnotation(SubSystem.class);
            boolean authServer = AuthenticationService.class.isAssignableFrom(clazz);
            int minCode = groupAnnotation.minCode();
            int maxCode = groupAnnotation.maxCode();
            String groupName = groupAnnotation.name();
            Class<?> returnCodeClass = groupAnnotation.codeDefine();
            try {
                for (Field f : returnCodeClass.getDeclaredFields()) {
                    if (isConstField(f) && AbstractReturnCode.class.isAssignableFrom(f.getType())) {
                        AbstractReturnCode code = (AbstractReturnCode)f.get(null);
                        if (code.getCode() < minCode || code.getCode() >= maxCode) {
                            throw new RuntimeException(
                                    "code " + f.getName() + " which value is " + code.getCode() + " not in the scope [" + minCode + "," + maxCode
                                            + ") in " + groupName);
                        }
                        code.setName(f.getName() + UNDER_SCORE + code.getCode());
                        code.setService(groupName);
                        ReturnCodeContainer.putReturnCodeSuper2Map(code);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("parse code failed. " + returnCodeClass.getName() + " in " + groupName, e);
            }
            List<ApiMethodInfo> apis = new LinkedList<ApiMethodInfo>();
            for (Method mInfo : clazz.getMethods()) {
                HttpApi api = mInfo.getAnnotation(HttpApi.class);
                if (api != null) {
                    ApiMethodInfo apiInfo = new ApiMethodInfo();
                    apiInfo.type = ApiMethodInfo.Type.DUBBO;
                    if (CompileConfig.isDebug) {
                        ApiMockReturnObject aro = mInfo.getAnnotation(ApiMockReturnObject.class);
                        if (aro != null) {
                            apiInfo.staticMockValue = aro.value().newInstance();
                            apiInfo.mocked = true;
                            if (!mInfo.getReturnType().isInstance(apiInfo.staticMockValue) || !MockApiReturnObject.class
                                    .isInstance(apiInfo.staticMockValue)) {
                                throw new RuntimeException("mock data type error " + clazz.getName() + " " + api.name());
                            }
                        }
                    }
                    ApiShortCircuit asc = mInfo.getAnnotation(ApiShortCircuit.class);
                    if (asc != null) {
                        apiInfo.staticMockValue = asc.value().newInstance();
                        apiInfo.mocked = true;
                        if (!mInfo.getReturnType().isInstance(apiInfo.staticMockValue) || !MockApiReturnObject.class
                                .isInstance(apiInfo.staticMockValue)) {
                            throw new RuntimeException("short circuit data type error " + clazz.getName() + " " + api.name());
                        }
                    }
                    EncryptTransfer et = mInfo.getAnnotation(EncryptTransfer.class);
                    if (et != null) {
                        apiInfo.encryptionOnly = et.encryptionOnly();
                    }
                    ParamsExport pe = mInfo.getAnnotation(ParamsExport.class);
                    if (pe != null && pe.value() != null && pe.value().length > 0) {
                        apiInfo.exportParams = new HashMap<>(pe.value().length);
                        for (ParamExport n : pe.value()) {
                            apiInfo.exportParams.put(n.name(), n.dataType());
                        }
                    }
                    ParamExport pei = mInfo.getAnnotation(ParamExport.class);
                    if (pei != null) {
                        if (apiInfo.exportParams == null) {
                            apiInfo.exportParams = new HashMap<>();
                            apiInfo.exportParams.put(pei.name(), pei.dataType());
                        }
                    }
                    if (subSystem == null) {
                        subSystem = mInfo.getAnnotation(SubSystem.class);
                    }
                    if (subSystem != null) {
                        apiInfo.subSystemId = subSystem.value();
                    }
                    if (authServer && AuthenticationService.AuthMethodSignature
                            .equals(mInfo.getName() + org.objectweb.asm.Type.getMethodDescriptor(mInfo))) {
                        apiInfo.authenticationMethod = true;
                    }
                    apiInfo.groupName = groupName;
                    apiInfo.description = api.desc();
                    apiInfo.detail = api.detail();
                    apiInfo.methodName = api.name();
                    apiInfo.owner = api.owner();
                    apiInfo.groupOwner = groupAnnotation.owner();
                    SparseIntArray code2index = new SparseIntArray();
                    DesignedErrorCode errors = mInfo.getAnnotation(DesignedErrorCode.class);
                    if (errors != null && errors.value() != null) {
                        int[] es = errors.value();
                        int size = es.length;
                        if (size > 0) {
                            apiInfo.errorCodes = new AbstractReturnCode[size];
                            apiInfo.errors = new int[size];
                            for (int i = 0; i < size; i++) {
                                AbstractReturnCode c = ReturnCodeContainer.findCode(es[i]);
                                if (c.getDisplay() != c) {
                                    throw new RuntimeException(
                                            "cannot use a shadow code as a designed code " + c.getCode() + " in " + clazz.getName() + " " + api
                                                    .name());
                                }
                                apiInfo.errorCodes[i] = c;
                                apiInfo.errors[i] = c.getCode();
                            }
                            // 避免重复定义error code 以及避免暴露隐藏code
                            HashSet<Integer> set = new HashSet<Integer>();
                            for (int i : es) {
                                if (set.contains(i)) {
                                    throw new RuntimeException("duplicate error code " + i + " in " + clazz.getName() + " " + api.name());
                                } else {
                                    set.add(i);
                                }
                            }
                            // error code 排序
                            Arrays.sort(apiInfo.errorCodes, new Comparator<AbstractReturnCode>() {
                                @Override
                                public int compare(AbstractReturnCode o1, AbstractReturnCode o2) {
                                    return o1.getCode() > o2.getCode() ? 1 : o1.getCode() < o2.getCode() ? -1 : 0;
                                }
                            });
                            Arrays.sort(apiInfo.errors);
                            for (int i = 0; i < apiInfo.errors.length; i++) {
                                code2index.put(apiInfo.errors[i], i);
                            }
                        }
                    }
                    ErrorCodeMapping errMapping = mInfo.getAnnotation(ErrorCodeMapping.class);
                    if (errMapping != null) {
                        // 获得一个从内部系统code指向当前系统code的映射表
                        SparseIntArray map = new SparseIntArray();
                        String methodName = mInfo.getName();
                        setErrorCodeMapping(map, errMapping.mapping(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping1(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping2(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping3(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping4(), methodName);

                        setErrorCodeMapping(map, errMapping.mapping5(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping6(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping7(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping8(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping9(), methodName);

                        setErrorCodeMapping(map, errMapping.mapping10(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping11(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping12(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping13(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping14(), methodName);

                        setErrorCodeMapping(map, errMapping.mapping15(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping16(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping17(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping18(), methodName);
                        setErrorCodeMapping(map, errMapping.mapping19(), methodName);

                        int len = map.size();
                        // 更新上述映射表成为内部系统code指向当前声明的designed error code数组下标的映射表以便在运行时获取designed error code强类型信息
                        for (int i = 0; i < len; i++) {
                            int index = code2index.get(map.valueAt(i), -1);
                            if (index == -1) {
                                throw new RuntimeException("undefined designed error code " + map.valueAt(i) + " at " + apiInfo.methodName
                                        + " when inner code mapping");
                            }
                            // 检测内部code是否错误的填充了当前系统的code
                            int innerCode = map.keyAt(i);
                            if (innerCode >= minCode && innerCode < maxCode) {
                                throw new RuntimeException(innerCode + " is not an inner code, at " + apiInfo.methodName
                                        + " when inner code mapping");
                            }
                            map.put(innerCode, index);
                        }

                        apiInfo.innerCodeMap = map;
                    }
                    apiInfo.proxyMethodInfo = mInfo;
                    Class<?>[] parameterTypes = mInfo.getParameterTypes();
                    Annotation[][] parameterAnnotations = mInfo.getParameterAnnotations();
                    if (parameterTypes.length != parameterAnnotations.length) {
                        throw new RuntimeException("存在未被标记的http api参数" + clazz.getName());
                    }
                    // 校验授权接口注释是否符合规范
                    if (apiInfo.authenticationMethod) {
                        if (parameterAnnotations[0].length == 0
                                || parameterAnnotations[0][0].getClass() != ApiAutowired.class
                                || !AutowireableParameter.userid.equals(((ApiAutowired)parameterAnnotations[0][0]).value())) {
                            throw new RuntimeException("authentication method must tag the first parameter as Autowired(userid).");
                        }
                        if (parameterAnnotations[1].length == 0
                                || parameterAnnotations[1][0].getClass() != ApiParameter.class
                                || ((ApiParameter)parameterAnnotations[1][0]).enumDef() == EnumNull.class) {
                            throw new RuntimeException("authentication method must tag the second parameter as ApiParameter with a enum define.");
                        }
                        if (parameterAnnotations[2].length == 0
                                || parameterAnnotations[2][0].getClass() != ApiParameter.class) {
                            throw new RuntimeException("authentication method must tag the third parameter as ApiParameter");
                        }
                    }
                    ApiParameterInfo[] pInfos = new ApiParameterInfo[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        ApiParameterInfo pInfo = new ApiParameterInfo();
                        pInfo.type = parameterTypes[i];
                        Annotation[] a = parameterAnnotations[i];
                        if (a == null || a.length == 0) {
                            throw new RuntimeException("api参数未被标记" + clazz.getName());
                        }
                        {
                            //入参的递归检查
                            if (Collection.class.isAssignableFrom(pInfo.type)) {//增加对List自定义Object的支持+List<String>的支持
                                if (Collection.class.isAssignableFrom(pInfo.type)) {
                                    Type genericType;
                                    try {
                                        genericType = ((ParameterizedType)mInfo.getGenericParameterTypes()[i]).getActualTypeArguments()[0];
                                    } catch (Throwable t) {
                                        throw new RuntimeException("unsupported input type,get genericType failed, method name:" + mInfo.getName(),
                                                t);
                                    }
                                    try {
                                        // 接口入参暂不支持使用动态类型 DynamicEntity
                                        pInfo.actuallyGenericType = Class
                                                .forName(((Class)genericType).getName(), true, Thread.currentThread().getContextClassLoader());
                                    } catch (Exception e) {
                                        throw new RuntimeException("generic type unsupported:" + genericType + " in " + clazz.getName(), e);
                                    }
                                } else {
                                    throw new RuntimeException("only list is support when using collection, method name:" + mInfo.getName());
                                }
                            }
                            if (a[0].annotationType() != ApiCookieAutowired.class || pInfo.type != Map.class) {
                                TypeCheckUtil.recursiveCheckInputType(clazz.getName(), pInfo.type, pInfo.actuallyGenericType,
                                        new DescriptionAnnotationChecker(), new SerializableImplChecker(),
                                        new PublicFieldChecker());
                            }
                        }
                        for (int j = 0; j < a.length; ) {
                            Annotation n = a[j];
                            if (n.annotationType() == ApiParameter.class) {
                                ApiParameter p = (ApiParameter)n;
                                pInfo.description = p.desc();
                                if (p.sequence() != null && p.sequence().trim().length() > 0) {
                                    pInfo.sequence = p.sequence();
                                }
                                pInfo.isRsaEncrypted = p.rsaEncrypted();
                                pInfo.ignoreForSecurity = p.ignoreForSecurity();
                                pInfo.name = p.name();
                                pInfo.injectable = (p.serviceInject() == null || p.serviceInject() == ServiceInjectable.class) ?
                                        null : p.serviceInject().newInstance();
                                pInfo.isRequired = pInfo.injectable == null ? p.required() : false;
                                if (p.enumDef() != null && p.enumDef() != EnumNull.class) {
                                    if (pInfo.type == String.class || pInfo.type.getComponentType() == String.class
                                            || pInfo.actuallyGenericType == String.class) {
                                        pInfo.verifyEnumType = p.enumDef();
                                    }
                                }
                                if (CompileConfig.isDebug) {
                                    // 检查sequence属性合法性
                                    if (pInfo.sequence != null && pInfo.sequence.length() > 0) {
                                        try {
                                            if (pInfo.sequence.startsWith("int") || pInfo.sequence.startsWith("str")) {
                                                Integer.parseInt(pInfo.sequence.substring(4));
                                            } else {
                                                throw new RuntimeException("invalid sequence value("
                                                        + pInfo.sequence + ") of api parameter " + mInfo.getName() + "  " + pInfo.name);
                                            }
                                        } catch (Exception e) {
                                            throw new RuntimeException("invalid sequence value("
                                                    + pInfo.sequence + ") of api parameter " + mInfo.getName() + "  " + pInfo.name, e);
                                        }
                                    }
                                    // '_'前缀被用于标识本系统的通用参数
                                    // 除了第三方接口外, 其余接口不能使用'_'打头的参数名
                                    if (!SecurityType.Integrated.check(api.security())) {
                                        if (pInfo.name.startsWith("_")) {
                                            throw new RuntimeException(
                                                    "api parameter name cannot start with '_'" + mInfo.getName() + "  " + pInfo.name);
                                        }
                                    }
                                }
                                pInfo.verifyRegex = p.verifyRegex();
                                if (pInfo.verifyRegex == null) {
                                    pInfo.verifyMsg = null;
                                } else {
                                    if (pInfo.verifyRegex.length() == 0) {
                                        pInfo.verifyRegex = null;
                                        pInfo.verifyMsg = null;
                                    } else {
                                        pInfo.verifyMsg = p.verifyMsg();
                                        if (pInfo.verifyMsg == null) {
                                            throw new RuntimeException(
                                                    "verifyMsg must not null when verifyRegex is not null. method:" + apiInfo.methodName
                                                            + "  parameter:" + pInfo.name);
                                        }
                                    }
                                }
                                try {
                                    if (pInfo.isRequired) {
                                        pInfo.defaultValue = null;
                                    } else {
                                        parseDefaultValue(p.defaultValue(), apiInfo, pInfo);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(
                                            "parse default value failed. " + pInfo.name + "  " + api.name() + "  " + clazz.getName(), e);
                                }
                                break;
                            } else if (n.annotationType() == ApiAutowired.class) {
                                ApiAutowired p = (ApiAutowired)n;
                                pInfo.name = p.value().name();
                                pInfo.isRequired = false;
                                if (p.value() == AutowireableParameter.serviceInjection) {
                                    pInfo.injectable = (p.serviceInject() == null || p.serviceInject() == ServiceInjectable.class) ?
                                            null : p.serviceInject().newInstance();
                                }
                                if (AutowireableParameter.postBody.name().equals(pInfo.name) && parameterTypes.length != 1) {
                                    throw new RuntimeException(
                                            "one parameter only for postBody " + api.name() + "  " + clazz.getName());
                                }
                                pInfo.isAutowired = true;
                                break;
                            } else if (n.annotationType() == DesignedParameter.class) {
                                DesignedParameter p = (DesignedParameter)n;
                                pInfo.creator = (ParamCreator)p.value().newInstance();
                                pInfo.isRequired = true;
                                pInfo.isAutowired = true;
                                break;
                            } else if (n.annotationType() == ApiCookieAutowired.class) {
                                ApiCookieAutowired p = (ApiCookieAutowired)n;
                                if (p.value() == null || p.value().length == 0) {
                                    throw new RuntimeException("cookie名不能为空 " + api.name() + "  " + clazz.getName());
                                }
                                pInfo.name = AutowireableParameter.cookies.name();
                                pInfo.extInfos = p.value();
                                pInfo.isAutowired = true;
                                break;
                            } else if (n.annotationType() == ApiHeaderAutowired.class) {
                                ApiHeaderAutowired p = (ApiHeaderAutowired)n;
                                if (p.value() == null || p.value().length == 0) {
                                    throw new RuntimeException("header名不能为空 " + api.name() + "  " + clazz.getName());
                                }
                                pInfo.name = AutowireableParameter.headers.name();
                                pInfo.extInfos = p.value();
                                pInfo.isAutowired = true;
                                break;
                            }
                            j++;
                            if (j == a.length) {
                                throw new RuntimeException("api参数 " + pInfo.name + " 未被标记 " + api.name() + "  " + clazz.getName());
                            }
                        }
                        if (pInfo.injectable != null) {
                            Class dataType = pInfo.injectable.getDataType();
                            boolean pass = false;
                            try {
                                Type[] ts = dataType.getGenericInterfaces();
                                if (ts.length > 0) {
                                    pass = ((ParameterizedType)ts[0]).getActualTypeArguments()[0] == pInfo.type;
                                }
                            } catch (Exception e) {
                                logger.error("check injectable failed. " + pInfo.name + "  " + api.name() + "  " + clazz.getName(), e);
                            }
                            if (!pass) {
                                throw new RuntimeException("api参数 " + pInfo.name + " 类型与注入类型不匹配 " + api.name() + "  " + clazz.getName());
                            }
                        }
                        pInfos[i] = pInfo;
                    }
                    apiInfo.parameterInfos = pInfos;
                    if (hasDuplicateParam(pInfos)) {
                        throw new RuntimeException("duplicate param , groupName: " + apiInfo.groupName + ", methodName: " + apiInfo.methodName);
                    }
                    parseReturnType(apiInfo, mInfo, clazz);//返回结果解析,设置apiInfo.seriliazer,apiInfo.returnType, apiInfo.actuallyGenericType
                    //递归检查返回结果类型
                    TypeCheckUtil.recursiveCheckReturnType(clazz.getName(), apiInfo.returnType, apiInfo.actuallyGenericReturnType,
                            new SerializableImplChecker(), new DescriptionAnnotationChecker(),
                            new PublicFieldChecker());
                    apiInfo.dubboInterface = clazz;
                    apiInfo.securityLevel = api.security();
                    //对于Integrated级别接口需要指定可访问该接口的第三方编号
                    if (SecurityType.Integrated.check(apiInfo.securityLevel)) {
                        if (api.needVerify()) {
                            apiInfo.needVerfiy = true;
                        } else {
                            // 接口未要求网关进行签名验证
                            apiInfo.needVerfiy = false;
                            logger.warn(
                                    "integrated api:" + apiInfo.methodName
                                            + " do not need verify by apigw. Dubbo service must ensure the legitimacy of the request!");
                        }
                    }
                    apiInfo.state = api.state();
                    apis.add(apiInfo);
                }
            }
            ApiMockInterfaceImpl amii = clazz.getAnnotation(ApiMockInterfaceImpl.class);
            if (amii != null) {
                for (ApiMethodInfo info : apis) {
                    info.mocked = true;
                }
            }
            if (apis.size() == 0) {
                throw new RuntimeException("[API] api method not found. class:" + clazz.getName());
            }
            return apis;
        } catch (Throwable t) {
            logger.error("parse api failed. " + clazz.getName(), t);
            if (CompileConfig.isDebug) {
                t.printStackTrace();//为testcase服务,控制台打印错误信息
            }
        }
        return null;
    }

    /**
     * 默认值是否合法的检查
     * 设置默认值
     */
    private static void parseDefaultValue(String defaultValue, ApiMethodInfo apiInfo, ApiParameterInfo pInfo) {
        pInfo.defaultValue = defaultValue;
        if (pInfo.type.isPrimitive()) {
            if ((pInfo.defaultValue == null || pInfo.defaultValue.length() == 0)) {
                pInfo.defaultValue = "0";//未设置默认值,0
            } else {
                if (pInfo.type == boolean.class) {
                    Boolean.parseBoolean(pInfo.defaultValue);
                } else if (pInfo.type == byte.class) {
                    Byte.parseByte(pInfo.defaultValue);
                } else if (pInfo.type == short.class) {
                    Short.parseShort(pInfo.defaultValue);
                } else if (pInfo.type == char.class) {
                    Integer.parseInt(pInfo.defaultValue);
                } else if (pInfo.type == int.class) {
                    Integer.parseInt(pInfo.defaultValue);
                } else if (pInfo.type == long.class) {
                    Long.parseLong(pInfo.defaultValue);
                } else if (pInfo.type == float.class) {
                    Float.parseFloat(pInfo.defaultValue);
                } else if (pInfo.type == double.class) {
                    Double.parseDouble(pInfo.defaultValue);
                }
            }
        } else if (pInfo.type.isEnum()) {
            if (pInfo.defaultValue != null) {
                if (pInfo.defaultValue.length() == 0) {
                    pInfo.defaultValue = null;
                } else {
                    Enum.valueOf((Class<Enum>)pInfo.type, pInfo.defaultValue);
                }
            }
        } else if ((pInfo.type.isArray() && pInfo.type.getComponentType() == String.class) || (Collection.class.isAssignableFrom(pInfo.type)
                && pInfo.actuallyGenericType == String.class)) {
            apiInfo.needDefaultValueConstDefined = true;//需要在ApiExecute设置常量
            pInfo.needDefaultValueConstDefined = true;
            if (pInfo.defaultValue != null && pInfo.defaultValue.length() == 0) {
                pInfo.defaultValue = null;//未设置String的默认值,默认值为null
            } else if (pInfo.verifyEnumType != null && pInfo.verifyEnumType != EnumNull.class) {
                List<String> sa = JSON.parseArray(pInfo.defaultValue, String.class);
                for (String s : sa) {
                    Enum.valueOf(pInfo.verifyEnumType, s);
                }
            }
        } else if (pInfo.type != String.class) {
            apiInfo.needDefaultValueConstDefined = true;//需要在ApiExecute设置常量
            pInfo.needDefaultValueConstDefined = true;
            if (pInfo.defaultValue != null) {
                if (pInfo.defaultValue.length() == 0) {
                    pInfo.defaultValue = null;//结构化入参默认值为null
                } else {
                    JSON.parseObject(pInfo.defaultValue, pInfo.type);//测试结构化参数
                }
            }
        } else if (pInfo.type == String.class) {
            if (pInfo.defaultValue != null && pInfo.defaultValue.length() == 0) {
                pInfo.defaultValue = null;//未设置String的默认值,默认值为null
            } else if (pInfo.verifyEnumType != null && pInfo.verifyEnumType != EnumNull.class) {
                Enum.valueOf(pInfo.verifyEnumType, pInfo.defaultValue);
            }
        }
    }

    /**
     * 解析返回结果,为每个api设定对应的serializer
     *
     * @param apiInfo api信息,保存了接口对应的处理实例、serializer等信息
     * @param mInfo   反射获得的方法信息
     * @param clazz   对应的dubbo service interface class
     */
    private static void parseReturnType(ApiMethodInfo apiInfo, Method mInfo, Class clazz) {
        apiInfo.returnType = mInfo.getReturnType();
        apiInfo.wrapper = ResponseWrapper.objectWrapper;
        //返回结果分析
        if (String.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(StringResp.class);
            apiInfo.wrapper = ResponseWrapper.stringWrapper;
        } else if (String[].class == apiInfo.returnType) {//不适用ObjectArrayResp，考虑到代码生成的时候会生成重复代码
            apiInfo.serializer = POJOSerializerProvider.getSerializer(StringArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.stringArrayWrapper;
        } else if (Date.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(StringResp.class);
            apiInfo.wrapper = ResponseWrapper.dateWrapper;
        } else if (Date[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(StringArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.dateArrayWrapper;
        } else if (boolean.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(BoolResp.class);
            apiInfo.wrapper = ResponseWrapper.boolWrapper;
        } else if (boolean[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(BoolArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.boolArrayWrapper;
        } else if (byte.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberResp.class);
            apiInfo.wrapper = ResponseWrapper.byteWrapper;
        } else if (short.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberResp.class);
            apiInfo.wrapper = ResponseWrapper.shortWrapper;
        } else if (char.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberResp.class);
            apiInfo.wrapper = ResponseWrapper.charWrapper;
        } else if (int.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberResp.class);
            apiInfo.wrapper = ResponseWrapper.intWrapper;
        } else if (byte[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.byteArrayWrapper;
        } else if (short[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.shotArrayWrapper;
        } else if (char[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.charArrayWrapper;
        } else if (int[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(NumberArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.intArrayWrapper;
        } else if (long.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(LongResp.class);
            apiInfo.wrapper = ResponseWrapper.longWrapper;
        } else if (long[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(LongArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.longArrayWrapper;
        } else if (double.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(DoubleResp.class);
            apiInfo.wrapper = ResponseWrapper.doubleWrapper;
        } else if (float.class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(DoubleResp.class);
            apiInfo.wrapper = ResponseWrapper.floatWrapper;
        } else if (double[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(DoubleArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.doubleArrayWrapper;
        } else if (float[].class == apiInfo.returnType) {
            apiInfo.serializer = POJOSerializerProvider.getSerializer(DoubleArrayResp.class);
            apiInfo.wrapper = ResponseWrapper.floatArrayWrapper;
        } else if (JSONString.class == apiInfo.returnType) {
            apiInfo.serializer = Serializer.getJsonStringSerializer();
            apiInfo.wrapper = ResponseWrapper.objectWrapper;
        } else if (RawString.class == apiInfo.returnType) {
            apiInfo.serializer = Serializer.getRawStringSerializer();
            apiInfo.wrapper = ResponseWrapper.objectWrapper;
        } else if (Collection.class.isAssignableFrom(apiInfo.returnType)) {//增加对Collection自定义Object的支持+Collection<String>的支持
            Class<?> genericClazz = TypeCheckUtil.getSupportedGenericClass(mInfo.getGenericReturnType(), mInfo.getName() + " return type.");
            if (String.class == genericClazz) {//如果要支持更多的jdk中已有类型的序列化
                apiInfo.serializer = POJOSerializerProvider.getSerializer(StringArrayResp.class);
                apiInfo.wrapper = ResponseWrapper.stringCollectionWrapper;
            } else if (genericClazz.getAnnotation(Description.class) != null) {
                apiInfo.serializer = Serializer.getObjectArrayRespSerializer();
                apiInfo.wrapper = ResponseWrapper.objectCollectionWrapper;
            } else if (Date.class == genericClazz) {
                apiInfo.serializer = POJOSerializerProvider.getSerializer(DateArrayResp.class);
                apiInfo.wrapper = ResponseWrapper.dateCollectionWrapper;
            } else {
                throw new RuntimeException("unsupported return type, genericType:" + genericClazz.getName() + " method name:" + mInfo.getName());
            }
            apiInfo.actuallyGenericReturnType = genericClazz;
        } else if (apiInfo.returnType.isArray()) {//TODO 自定义数组的支持
            throw new RuntimeException("unsupported return type, method name:" + mInfo.getName());
        } else {
            Description desc = apiInfo.returnType.getAnnotation(Description.class);
            if (desc == null) {
                throw new RuntimeException(
                        "unsupported return type:" + apiInfo.returnType.getName() + ", miss description method name:" + mInfo.getName());
            }
            apiInfo.serializer = POJOSerializerProvider.getSerializer(apiInfo.returnType);
            apiInfo.wrapper = ResponseWrapper.objectWrapper;
        }
    }

    /**
     * 检查接口参数定义中是否有重复的参数命名
     *
     * @param pInfos 参数信息
     *
     * @return true:存在重复的参数名,false:无重复参数
     */
    private static boolean hasDuplicateParam(ApiParameterInfo[] pInfos) {
        HashSet<String> hs = new HashSet<String>();
        if (pInfos != null) {
            for (ApiParameterInfo pInfo : pInfos) {
                if (!hs.contains(pInfo.name)) {
                    hs.add(pInfo.name);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private static void setErrorCodeMapping(SparseIntArray map, int[] mapping, String methodName) {
        if (mapping != null && mapping.length > 0) {
            if (mapping.length % 2 > 0) {
                StringBuilder sb = new StringBuilder("invalid error code mapping(1): ");
                for (int i = 0; i < mapping.length; i++) {
                    sb.append(mapping[i]).append(",");
                }
                throw new RuntimeException(sb.append(" in ").append(methodName).toString());
            }
            for (int i = 0; i < mapping.length; ) {
                if (mapping[i] <= 0 || mapping[i + 1] <= 0) {
                    throw new RuntimeException("invalid error code mapping(2): " + mapping[i] + "=>" + mapping[i + 1] + " in " + methodName);
                }
                if (map.get(mapping[i + 1]) > 0) {
                    throw new RuntimeException("duplicate inner code mapping: " + map.get(mapping[i + 1]));
                }
                map.put(mapping[i + 1], mapping[i]);

                i += 2;
            }
        }
    }
}
