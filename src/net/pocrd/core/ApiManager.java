package net.pocrd.core;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.HttpApi;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.util.ClassUtil;

public class ApiManager {
    public static interface HttpApiExecuter {
        Object execute(String[] parameters);
    }

    private static final String                             API_METHOD_NAME = "execute";
    private HashMap<String, HttpApiExecuter>                nameToApi       = new HashMap<String, HttpApiExecuter>();
    private HashMap<String, ApiMethodInfo>                  apiInfos        = new HashMap<String, ApiMethodInfo>();

    public ApiManager(String packageName) {
        // TODO:需要开发一个编译器plugin在编译期判断返回值是否合法(基本类型，特殊类型或者特殊的泛型类型)
        registerAll(packageName);
    }

    public ApiMethodInfo getApiMethodInfo(String name) {
        return apiInfos.get(name);
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
                        register(groupAnnotation.name(), clazz);
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
                HttpApi api = mInfo.getAnnotation(HttpApi.class);
                if (api != null) {

                }
            }
        }
        if (!found) {
            throw new RuntimeException(API_METHOD_NAME + " method not found. class:" + clazz.getName());
        }
    }
}
