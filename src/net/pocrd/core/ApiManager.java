package net.pocrd.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.ApiParameter;
import net.pocrd.annotation.HttpApi;
import net.pocrd.define.HttpApiExecuter;
import net.pocrd.define.Serializer;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.ApiParameterInfo;
import net.pocrd.util.ClassUtil;
import net.pocrd.util.CommonConfig;
import net.pocrd.util.HttpApiProvider;
import net.pocrd.util.SerializerProvider;

public class ApiManager {
    private static final String              API_METHOD_NAME = "execute";
    private static final ApiMethodInfo[]     empty           = new ApiMethodInfo[0];
    private HashMap<String, HttpApiExecuter> nameToApi       = new HashMap<String, HttpApiExecuter>();
    private HashMap<String, ApiMethodInfo>   apiInfos        = new HashMap<String, ApiMethodInfo>();

    public ApiManager(String packageName) {
        // TODO:需要开发一个编译器plugin在编译期判断返回值是否合法(基本类型，特殊类型或者特殊的泛型类型)
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
        return nameToApi.get(name.toLowerCase()).execute(parameters);
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
                    apiInfo.methodName = api.name().toLowerCase();
                    DesignedErrorCode errors = mInfo.getAnnotation(DesignedErrorCode.class);
                    if (errors != null) {
                        apiInfo.errorCodes = errors.value();
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
                        if (CommonConfig.isDebug) {
                            if (!type.isPrimitive() && type != String.class && type != Boolean.class && type != Byte.class && type != Character.class
                                    && type != Short.class && type != Integer.class && type != Long.class && type != Float.class
                                    && type != Double.class) {
                                throw new RuntimeException("不支持的参数类型" + clazz.getName() + " " + type.getName());
                            }
                        }
                        Annotation[] a = parameterAnnotations[i];
                        pInfo.type = type.getName();
                        pInfo.setRawType(type);// pInfo.setRawDefaultValue(v);
                        if (a == null) {
                            throw new RuntimeException("api参数未被标记" + clazz.getName());
                        }
                        for (int j = 0; j < a.length;) {
                            Annotation n = a[j];
                            if (n.annotationType() == ApiParameter.class) {
                                ApiParameter p = (ApiParameter)n;
                                pInfo.defaultValue = p.defaultValue();
                                pInfo.description = p.desc();
                                pInfo.isRequired = p.required();
                                pInfo.name = p.name();
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
                    apiInfo.returnTypeString = apiInfo.returnType.getName();
                    if (CommonConfig.isDebug) {
                        Class<?> type = apiInfo.returnType;
                        if (!apiInfo.returnTypeString.startsWith("net.pocrd.api.resp.Api") && type != String.class) {
                            throw new RuntimeException("不支持的返回值类型" + clazz.getName() + " " + type.getName());
                        }
                    }
                    // 移除命名空间
                    // if (apiInfo.returnTypeString.contains("$")) {
                    // apiInfo.returnTypeString = apiInfo.returnTypeString.substring(apiInfo.returnTypeString.lastIndexOf('$') + 1);
                    // } else if (apiInfo.returnTypeString.contains(".")) {
                    // apiInfo.returnTypeString = apiInfo.returnTypeString.substring(apiInfo.returnTypeString.lastIndexOf('.') + 1);
                    // }
                    if (apiInfo.returnType == String.class) {
                        apiInfo.serializer = Serializer.stringSerializer;
                    } else {
                        apiInfo.serializer = SerializerProvider.getSerializer(apiInfo.returnType);
                    }
                    apiInfo.securityLevel = apiInfo.securityLevel;
                    apiInfo.state = apiInfo.state;
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
