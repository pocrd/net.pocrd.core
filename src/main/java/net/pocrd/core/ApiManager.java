package net.pocrd.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.ApiParameter;
import net.pocrd.annotation.DesignedErrorCode;
import net.pocrd.annotation.HttpApi;
import net.pocrd.define.CompileConfig;
import net.pocrd.define.HttpApiExecuter;
import net.pocrd.define.Serializer;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.ApiParameterInfo;
import net.pocrd.entity.ReturnCode;
import net.pocrd.util.ClassUtil;
import net.pocrd.util.HttpApiProvider;
import net.pocrd.util.ProtobufSerializerProvider;

public final class ApiManager {
    private static final String              API_METHOD_NAME = "execute";
    private static final ApiMethodInfo[]     empty           = new ApiMethodInfo[0];
    private HashMap<String, HttpApiExecuter> nameToApi       = new HashMap<String, HttpApiExecuter>();
    private HashMap<String, ApiMethodInfo>   apiInfos        = new HashMap<String, ApiMethodInfo>();
    private String                           entityPrefix;

    public ApiManager(String packageName, String entityPrefix) {
        this.entityPrefix = entityPrefix;
        registerAll(packageName);
    }

    public ApiMethodInfo getApiMethodInfo(String name) {
        return apiInfos.get(name);
    }

    public ApiMethodInfo[] getApiMethodInfos() {
        return (ApiMethodInfo[])apiInfos.values().toArray(empty);
    }

    /**
     * 处理Api请求
     * 
     * @param name name来标识特定的Api请求
     * @param parameters Api请求参数
     * @return
     */
    public final Object processRequest(String name, String[] parameters) {
        return nameToApi.get(name).execute(parameters);
    }

    private void registerAll(String packageName) {
        try {
            Class<?>[] classes = ClassUtil.getAllClassesInPackage(packageName);
            if (classes != null && classes.length > 0) {
                for (Class<?> clazz : classes) {
                    ApiGroup groupAnnotation = clazz.getAnnotation(ApiGroup.class);
                    if (groupAnnotation != null) {
                        register(groupAnnotation.value(), clazz);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("api manager init failed.", e);
        }
    }

    private void register(String groupName, Class<?> clazz) {
        boolean found = false;
        for (Method mInfo : clazz.getMethods()) {
            if (API_METHOD_NAME.equals(mInfo.getName())) {
                found = true;
                HttpApi api = mInfo.getAnnotation(HttpApi.class);
                if (api != null) {
                    ApiMethodInfo apiInfo = new ApiMethodInfo();
                    apiInfo.groupName = groupName;
                    apiInfo.description = api.desc();
                    apiInfo.methodName = api.name();
                    DesignedErrorCode errors = mInfo.getAnnotation(DesignedErrorCode.class);
                    if (errors != null && errors.value() != null) {
                        int[] es = errors.value();
                        int size = es.length;
                        if (size > 0) {
                            apiInfo.errorCodes = new ReturnCode[size];
                            apiInfo.errors = new int[size];
                            for (int i = 0; i < size; i++) {
                                ReturnCode c = ReturnCode.findCode(es[i]);
                                apiInfo.errorCodes[i] = c;
                                apiInfo.errors[i] = c.getCode();
                            }
                            // 避免重复定义error code
                            if (CompileConfig.isDebug) {
                                HashSet<Integer> set = new HashSet<Integer>();
                                for (int i = 0; i < size; i++) {
                                    if (set.contains(es[i])) {
                                        throw new RuntimeException("duplicate error code " + es[i] + " in " + clazz.getName());
                                    } else {
                                        set.add(es[i]);
                                    }
                                }
                            }
                            // error code 排序
                            Arrays.sort(apiInfo.errorCodes, new Comparator<ReturnCode>() {

                                @Override
                                public int compare(ReturnCode o1, ReturnCode o2) {
                                    return o1.getCode() - o2.getCode();
                                }
                            });

                            Arrays.sort(apiInfo.errors);
                        }
                    }
                    apiInfo.proxyMethodInfo = mInfo;
                    Class<?>[] parameterTypes = mInfo.getParameterTypes();
                    Annotation[][] parameterAnnotations = mInfo.getParameterAnnotations();
                    if (parameterTypes.length != parameterAnnotations.length) {
                        throw new RuntimeException("存在未被标记的http api参数" + clazz.getName());
                    }
                    ApiParameterInfo[] pInfos = new ApiParameterInfo[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        ApiParameterInfo pInfo = new ApiParameterInfo();
                        Class<?> type = parameterTypes[i];
                        if (CompileConfig.isDebug) {
                            if (!type.isPrimitive() && type != String.class) {
                                throw new RuntimeException("不支持的参数类型" + clazz.getName() + " " + type.getName());
                            }
                        }
                        Annotation[] a = parameterAnnotations[i];
                        pInfo.type = type;
                        if (a == null) {
                            throw new RuntimeException("api参数未被标记" + clazz.getName());
                        }
                        for (int j = 0; j < a.length;) {
                            Annotation n = a[j];
                            if (n.annotationType() == ApiParameter.class) {
                                ApiParameter p = (ApiParameter)n;
                                pInfo.description = p.desc();
                                pInfo.isRequired = p.required();
                                pInfo.name = p.name();
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
                                            throw new RuntimeException("verifyMsg must not null when verifyRegex is not null. method:"
                                                    + apiInfo.methodName + "  parameter:" + pInfo.name);
                                        }
                                    }
                                }
                                if (pInfo.isRequired) {
                                    pInfo.defaultValue = null;
                                } else {
                                    pInfo.defaultValue = p.defaultValue();
                                    if (pInfo.defaultValue == null && pInfo.type.isPrimitive()) {
                                        throw new RuntimeException("nullable primitive parameter must has a default value. " + pInfo.name);
                                    }
                                }
                                break;
                            }
                            j++;
                            if (j == a.length) {
                                throw new RuntimeException("api参数未被标记" + clazz.getName());
                            }
                        }
                        pInfos[i] = pInfo;
                    }
                    apiInfo.parameterInfos = pInfos;
                    apiInfo.returnType = mInfo.getReturnType();
                    if (CompileConfig.isDebug) {
                        Class<?> type = apiInfo.returnType;
                        if (!apiInfo.returnType.getName().startsWith(entityPrefix) && type != String.class) {
                            throw new RuntimeException("不支持的返回值类型" + clazz.getName() + " " + type.getName());
                        }
                    }

                    if (apiInfo.returnType == String.class) {
                        apiInfo.serializer = Serializer.stringSerializer;
                    } else {
                        apiInfo.serializer = ProtobufSerializerProvider.getSerializer(apiInfo.returnType);
                    }
                    apiInfo.securityLevel = api.security();
                    apiInfo.state = api.state();
                    if (apiInfos.containsKey(apiInfo.methodName)) {
                        throw new RuntimeException("duplicate definision for " + apiInfo.methodName);
                    }
                    apiInfos.put(apiInfo.methodName, apiInfo);
                    nameToApi.put(apiInfo.methodName, HttpApiProvider.getApiExecuter(apiInfo.methodName, apiInfo));
                }
            }
        }
        if (!found) {
            throw new RuntimeException(API_METHOD_NAME + " method not found. class:" + clazz.getName());
        }
    }
}
