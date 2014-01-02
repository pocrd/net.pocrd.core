package net.pocrd.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.ApiParameter;
import net.pocrd.annotation.DesignedErrorCode;
import net.pocrd.annotation.HttpApi;
import net.pocrd.define.ConstField;
import net.pocrd.define.HttpApiExecuter;
import net.pocrd.define.Serializer;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.ApiParameterInfo;
import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCode.CodeInfo;
import net.pocrd.util.CDataString;
import net.pocrd.util.ClassUtil;
import net.pocrd.util.CommonConfig;
import net.pocrd.util.HttpApiProvider;
import net.pocrd.util.SerializerProvider;

public final class ApiManager {
    private static final String              API_METHOD_NAME = "execute";
    private static final ApiMethodInfo[]     empty           = new ApiMethodInfo[0];
    private HashMap<String, HttpApiExecuter> nameToApi       = new HashMap<String, HttpApiExecuter>();
    private HashMap<String, ApiMethodInfo>   apiInfos        = new HashMap<String, ApiMethodInfo>();
    private HashMap<String, String>          protos          = new HashMap<String, String>();
    private String                           entityPrefix;

    public ApiManager(String packageName, String entityPrefix) {
        this.entityPrefix = entityPrefix;
        // TODO:需要开发一个编译器plugin在编译期判断返回值是否合法(基本类型，特殊类型或者特殊的泛型类型)
        protos = ClassUtil.getAllProtoInPackage(entityPrefix);
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
                            apiInfo.errorCodes = new CodeInfo[size];
                            for (int i = 0; i < size; i++) {
                                ReturnCode c = ReturnCode.findCode(es[i]);
                                apiInfo.errorCodes[i] = new CodeInfo(c.getCode(), c.getName(), c.getDesc());
                            }
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
                        if (CommonConfig.isDebug) {
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
                                            throw new RuntimeException("verifyMsg should not null when verifyRegex is not null. method:"
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
                    if (CommonConfig.isDebug) {
                        Class<?> type = apiInfo.returnType;
                        if (!apiInfo.returnType.getName().startsWith(entityPrefix) && type != String.class) {
                            throw new RuntimeException("不支持的返回值类型" + clazz.getName() + " " + type.getName());
                        }
                    }

                    if (apiInfo.returnType == String.class) {
                        apiInfo.serializer = Serializer.stringSerializer;
                    } else {
                        apiInfo.serializer = SerializerProvider.getSerializer(apiInfo.returnType);
                        String[] ts = getTypeSchema(apiInfo.returnType);
                        if (ts != null && ts.length > 0) {
                            apiInfo.returnInfo = new CDataString[ts.length];
                            for (int i = 0; i < ts.length; i++) {
                                apiInfo.returnInfo[i] = new CDataString(ts[i]);
                            }
                        }
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

    private String[] getTypeSchema(Class<?> clazz) {
        LinkedList<String> list = new LinkedList<String>();
        HashSet<String> ns = new HashSet<String>();
        String name = clazz.getSimpleName();
        ns.add(name);
        if (protos.containsKey(name)) {
            list.add(protos.get(name));
            fillProtoTypeDependence(clazz, ns);
            for (String n : ns) {
                if (protos.containsKey(n)) {
                    if (!name.equals(n)) {
                        list.add(protos.get(n));
                    }
                } else {
                    throw new RuntimeException("proto file missing. " + n);
                }
            }
        } else {
            // TODO:通过反射构建type schema
            throw new RuntimeException("not a proto type. " + name);
        }
        return list.toArray(ConstField.EMPTY_STRING_ARRAY);
    }

    private void fillProtoTypeDependence(Class<?> clazz, HashSet<String> ns) {
        for (Method m : clazz.getDeclaredMethods()) {
            if ((m.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                continue;
            }
            Class<?> t = m.getReturnType();
            String n = t.getSimpleName();
            if ("Builder".equals(n) || n.endsWith("OrBuilder")) {
                continue;
            }
            if (!ns.contains(n) && t.getName().startsWith(entityPrefix)) {
                ns.add(n);
                fillProtoTypeDependence(t, ns);
            }
        }
    }
}
