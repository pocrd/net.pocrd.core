package net.pocrd.core;

import net.pocrd.annotation.Description;
import net.pocrd.annotation.EnumDef;
import net.pocrd.define.CommonParameter;
import net.pocrd.document.*;
import net.pocrd.entity.AbstractReturnCode;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.ApiParameterInfo;
import net.pocrd.entity.ReturnCodeContainer;
import net.pocrd.responseEntity.*;
import net.pocrd.util.RawString;
import net.pocrd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiDocumentationHelper {
    private static final Logger logger = LoggerFactory.getLogger(ApiDocumentationHelper.class);
    private static final ConcurrentHashMap<String, TypeStruct> reqStructs = new ConcurrentHashMap<String, TypeStruct>();
    private static final ConcurrentHashMap<String, TypeStruct> respStructs = new ConcurrentHashMap<String, TypeStruct>();
    private static final ConcurrentHashMap<String, TypeStruct> vitualListStructs = new ConcurrentHashMap<String, TypeStruct>();
    private static final List<TypeStruct> emptyTypeStructList = new ArrayList<TypeStruct>(0);

    public Document getDocument(ApiMethodInfo[] apis) {
        try {
            Document document = new Document();
            document.apiList = new LinkedList<MethodInfo>();
            document.codeList = new LinkedList<CodeInfo>();
            document.respStructList = getRespTypeStruct("", Response.class, null);
            document.respStructList.addAll(getRespTypeStruct("", CreditNotification.class, null));
            document.respStructList.addAll(getRespTypeStruct("", MessageNotification.class, null));
            document.systemParameterInfoList = getSysParamInfo();
            HashSet<Integer> designCode = new HashSet<Integer>();
            Arrays.sort(apis, new Comparator<ApiMethodInfo>() {
                @Override
                public int compare(ApiMethodInfo o1, ApiMethodInfo o2) {
                    return o1.methodName.compareTo(o2.methodName);
                }
            });
            for (ApiMethodInfo info : apis) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.description = info.description;
                methodInfo.detail = info.detail;
                methodInfo.groupName = info.groupName;
                methodInfo.methodName = info.methodName;
                methodInfo.securityLevel = info.securityLevel.name();
                methodInfo.groupOwner = info.groupOwner;
                methodInfo.methodOwner = info.owner;
                methodInfo.encryptionOnly = info.encryptionOnly;
                methodInfo.needVerify = info.needVerfiy;
                if (RawString.class == info.returnType) {
                    methodInfo.returnType = "string";
                    methodInfo.respStructList = emptyTypeStructList;
                } else if (String.class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, StringResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, StringResp.class, null);
                } else if (info.returnType.isEnum()) {
                    throw new RuntimeException(
                            "unsupport return type, can not return enum without wrapper, type:" + info.returnType.getName() + " method:"
                                    + methodInfo.methodName);
                } else if (String[].class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, StringArrayResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, StringArrayResp.class, null);
                } else if (boolean.class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, BoolResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, BoolResp.class, null);
                } else if (boolean[].class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, BoolArrayResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, BoolArrayResp.class, null);
                } else if (byte.class == info.returnType || short.class == info.returnType || char.class == info.returnType
                        || int.class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, NumberResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, NumberResp.class, null);
                } else if (byte[].class == info.returnType || short[].class == info.returnType || char[].class == info.returnType
                        || int[].class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, NumberArrayResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, NumberArrayResp.class, null);
                } else if (long.class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, LongResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, LongResp.class, null);
                } else if (long[].class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, LongArrayResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, LongArrayResp.class, null);
                } else if (double.class == info.returnType || float.class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, DoubleResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, DoubleResp.class, null);
                } else if (double[].class == info.returnType || float[].class == info.returnType) {
                    methodInfo.returnType = getEntityName(info.groupName, DoubleArrayResp.class);
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, DoubleArrayResp.class, null);
                } else if (Collection.class.isAssignableFrom(info.returnType)) {
                    if (info.actuallyGenericReturnType == String.class) {
                        methodInfo.returnType = getEntityName(info.groupName, StringArrayResp.class);
                        methodInfo.respStructList = getRespTypeStruct(info.groupName, StringArrayResp.class, null);
                    } else {
                        methodInfo.returnType = getEntityName4CollectionAndArray(info.groupName, info.actuallyGenericReturnType);
                        methodInfo.respStructList = getRespTypeStruct(info.groupName, info.returnType, info.actuallyGenericReturnType);
                    }
                } else if (info.returnType.isArray()) {
                    //TODO: 未来要做对象数组的支持
                    throw new RuntimeException("unsupport array type,type:" + info.returnType.getName());
                } else {
                    methodInfo.respStructList = getRespTypeStruct(info.groupName, info.returnType, null);
                    methodInfo.returnType = getEntityName(methodInfo.groupName, info.returnType);
                }
                methodInfo.state = info.state.name();
                if (info.errorCodes != null) {
                    methodInfo.errorCodeList = new ArrayList<CodeInfo>(info.errorCodes.length);
                    for (AbstractReturnCode rc : info.errorCodes) {
                        CodeInfo c = new CodeInfo();
                        c.code = rc.getCode();
                        c.desc = rc.getDesc();
                        c.name = rc.getName();
                        designCode.add(c.code);
                        methodInfo.errorCodeList.add(c);
                    }
                }
                if (info.parameterInfos != null && info.parameterInfos.length > 0) {
                    methodInfo.parameterInfoList = getParamInfoList(info.groupName, info.parameterInfos);
                    Set<TypeStruct> reqStructSet = getParamTypeStruct(info.groupName, info.parameterInfos);
                    if (reqStructSet != null && reqStructSet.size() > 0) {
                        methodInfo.reqStructList = new ArrayList<TypeStruct>(reqStructSet);
                    }
                }
                document.apiList.add(methodInfo);
            }
            for (AbstractReturnCode rc : ReturnCodeContainer.getOpenCodes()) {
                CodeInfo c = new CodeInfo();
                c.code = rc.getCode();
                c.desc = rc.getDesc();
                c.name = rc.getName();
                c.service = rc.getService();
                if (c.code > 0) {
                    c.isDesign = designCode != null && designCode.contains(c.code);
                } else if (c.desc == null) {
                    c.desc = c.name;
                    c.isDesign = false;
                } else {
                    c.isDesign = true;
                }
                document.codeList.add(c);
            }
            return document;
        } catch (Exception e) {
            logger.error("parse xml for api info failed.", e);
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        }
        return null;
    }

    private Set<TypeStruct> getParamTypeStruct(String groupName, ApiParameterInfo[] parameterInfos) {
        Set<TypeStruct> reqStructsSet = new HashSet<TypeStruct>();
        for (ApiParameterInfo p : parameterInfos) {
            if (!p.isAutowired) {
                List<TypeStruct> tmp = getReqTypeStruct(groupName, p.type, p.actuallyGenericType);
                if (tmp != null && tmp.size() > 0) {
                    reqStructsSet.addAll(tmp);
                }
            }
        }
        return reqStructsSet;
    }

    private List<TypeStruct> getReqTypeStruct(String groupName, Class<?> clazz, Class<?> actuallyGenericType) {
        List<TypeStruct> list = new LinkedList<TypeStruct>();
        HashSet<Class<?>> cs = new HashSet<Class<?>>();
        String name;
        if (clazz.isPrimitive() || clazz == String.class || clazz.isEnum()) {
            return null;
        } else if ((Collection.class.isAssignableFrom(clazz) && actuallyGenericType == String.class) || String[].class == clazz
                || char[].class == clazz
                || byte[].class == clazz || short[].class == clazz || boolean[].class == clazz || int[].class == clazz || float[].class == clazz
                || long[].class == clazz || double[].class == clazz) {
            return null;
        } else if (clazz.isArray()) {
            if (!clazz.getComponentType().isEnum()) {
                name = getEntityName(groupName, clazz.getComponentType());
                list.add(getReqStruct(name, groupName, clazz.getComponentType()));
                cs.add(clazz.getComponentType());
                fillReqTypeDependence(clazz.getComponentType(), cs);
            } else {
                return null;
            }
        } else if (Collection.class.isAssignableFrom(clazz)) {
            if (actuallyGenericType == null) {
                throw new RuntimeException(
                        "get requestTypeStruct failed,miss actuallyGenericType,groupName:" + groupName + ",returnType:" + clazz.getName());
            }
            if (!hasPublicFields(actuallyGenericType)) {
                throw new RuntimeException(
                        "get public field failed, actuallyGenericType has no public field. groupName:" + groupName + ",actuallyGenericType:"
                                + actuallyGenericType.getName());
            }
            if (!actuallyGenericType.isEnum()) {
                name = getEntityName(groupName, actuallyGenericType);
                list.add(getReqStruct(name, groupName, actuallyGenericType));
                cs.add(actuallyGenericType);
                fillReqTypeDependence(actuallyGenericType, cs);
            } else {
                return null;
            }
        } else {
            if (!hasPublicFields(clazz)) {
                throw new RuntimeException(
                        "get public field failed, actuallyGenericType has not public field. groupName:" + groupName + "clazz:" + clazz.getName());
            }
            name = getEntityName(groupName, clazz);
            list.add(getReqStruct(name, groupName, clazz));
            cs.add(clazz);
            fillReqTypeDependence(clazz, cs);
        }
        for (Class<?> c : cs) {
            String n = getEntityName(groupName, c);
            if (name.equals(n)) {
                continue;
            }
            list.add(getReqStruct(n, groupName, c));
        }
        return list;
    }

    // 获取返回类型的结构化描述
    private List<TypeStruct> getRespTypeStruct(String groupName, Class<?> clazz, Class<?> actuallyGenericType) {
        List<TypeStruct> list = new LinkedList<TypeStruct>();
        HashSet<Class<?>> cs = new HashSet<Class<?>>();
        String name;
        if (clazz.isArray()) {
            //返回自定义数组也需要虚构List定义
            throw new RuntimeException("unsupport array type,groupName:" + groupName + "type:" + clazz.getName());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            if (actuallyGenericType == null) {
                throw new RuntimeException(
                        "get typeStruct failed,miss actuallyGenericType,groupname:" + groupName + ",returntype:" + clazz.getName());
            }
            if (!hasPublicFields(actuallyGenericType)) {
                throw new RuntimeException(
                        "get public field failed, actuallyGenericType has not public field. groupName:" + groupName + "actuallyGenericType:"
                                + actuallyGenericType.getName());
            }
            String objectArrayName = getEntityName4CollectionAndArray(groupName, actuallyGenericType);//构建虚假类Api_Xxx_Array
            list.add(getVirtualListRespStruct(objectArrayName, groupName, actuallyGenericType));//构建虚假类Api_Xxx_Array
            name = getEntityName(groupName, actuallyGenericType);
            list.add(getRespStruct(name, groupName, actuallyGenericType));
            cs.add(actuallyGenericType);
            fillTypeDependence(actuallyGenericType, cs);
        } else {
            if (!hasPublicFields(clazz)) {
                throw new RuntimeException(
                        "get public field failed, actuallyGenericType has not public field. groupName:" + groupName + "clazz:" + clazz.getName());
            }
            name = getEntityName(groupName, clazz);
            list.add(getRespStruct(name, groupName, clazz));
            cs.add(clazz);
            fillTypeDependence(clazz, cs);
        }
        for (Class<?> c : cs) {
            String n = getEntityName(groupName, c);
            if (name.equals(n)) {
                continue;
            }
            list.add(getRespStruct(n, groupName, c));
        }
        return list;
    }

    private List<ParameterInfo> getParamInfoList(String groupName, ApiParameterInfo[] paramters) {
        List<ParameterInfo> list = new ArrayList<ParameterInfo>(paramters.length);
        HashSet<String> sequenceSet = new HashSet<String>();
        for (ApiParameterInfo p : paramters) {
            if (!p.isAutowired) {
                ParameterInfo b = new ParameterInfo();
                if (p.defaultValue != null) {
                    b.defaultValue = p.defaultValue;
                }
                if (p.type.isPrimitive() && (p.defaultValue == null || p.defaultValue.length() == 0)) {
                    b.defaultValue = "0";
                } else if (p.type.isEnum() && (p.defaultValue == null || p.defaultValue.length() == 0)) {
                    b.defaultValue = null;
                }
                if (p.sequence != null && p.sequence.length() > 0) {
                    b.sequence = p.sequence;
                    sequenceSet.add(b.sequence);
                }
                b.isRsaEncrypt = p.isRsaEncrypted;
                b.isRequired = p.isRequired;
                b.name = p.name;
                b.description = p.description;
                b.isList = Collection.class.isAssignableFrom(p.type) || p.type.isArray();
                if (p.type.isPrimitive() || p.type == String.class || p.type.isEnum()) {
                    if (p.type.isEnum()) {
                        b.description = getEnumDescription(p.type, p.description);
                        b.type = "string";
                    } else if (p.type == String.class) {
                        b.type = "string";
                        if (p.verifyEnumType != null) {
                            b.description = getEnumDescription(p.verifyEnumType, p.description);
                        }
                    } else {
                        b.type = p.type.getSimpleName();
                    }
                }
                if (p.type == String[].class) {
                    b.type = "string";
                } else if (p.type == byte[].class) {
                    b.type = byte.class.getSimpleName();
                } else if (p.type == boolean[].class) {
                    b.type = boolean.class.getSimpleName();
                } else if (p.type == char[].class) {
                    b.type = char.class.getSimpleName();
                } else if (p.type == short[].class) {
                    b.type = short.class.getSimpleName();
                } else if (p.type == int[].class) {
                    b.type = int.class.getSimpleName();
                } else if (p.type == float[].class) {
                    b.type = float.class.getSimpleName();
                } else if (p.type == long[].class) {
                    b.type = long.class.getSimpleName();
                } else if (p.type == double[].class) {
                    b.type = double.class.getSimpleName();
                } else if (Collection.class.isAssignableFrom(p.type) || p.type.isArray() || isComplexType(p.type)) {
                    if (Collection.class.isAssignableFrom(p.type)) {
                        if (p.actuallyGenericType == null) {
                            throw new RuntimeException(
                                    "get getParamInfoList failed, miss actuallyGenericType, groupname:" + groupName + ",type:" + p.type);
                        }
                        if (p.actuallyGenericType.isEnum()) {
                            b.description = getEnumDescription(p.actuallyGenericType, p.description);
                            b.type = "string";
                        } else if (p.actuallyGenericType == String.class) {
                            b.type = "string";
                            if (p.verifyEnumType != null) {
                                b.description = getEnumDescription(p.verifyEnumType, p.description);
                            }
                        } else {
                            b.type = getEntityName(groupName, p.actuallyGenericType);
                        }
                    } else if (p.type.isArray()) {
                        if (p.type.getComponentType().isEnum()) {
                            b.description = getEnumDescription(p.type.getComponentType(), p.description);
                            b.type = "string";
                        } else if (p.actuallyGenericType == String.class) {
                            b.type = "string";
                            if (p.verifyEnumType != null) {
                                b.description = getEnumDescription(p.verifyEnumType, p.description);
                            }
                        } else {
                            b.type = getEntityName(groupName, p.type.getComponentType());
                        }
                    } else {
                        b.type = getEntityName(groupName, p.type);
                    }
                }
                if (p.verifyRegex != null) {
                    b.verifyMsg = p.verifyMsg;
                    b.verifyRegex = p.verifyRegex;
                }
                list.add(b);
            }
        }

        int intIndex = 0;
        int strIndex = 0;
        // 为尚未初始化的sequence赋值
        for (ParameterInfo info : list) {
            if (info.sequence == null || info.sequence.length() == 0) {
                if (!info.isList && (int.class.getSimpleName().equals(info.type)
                        || long.class.getSimpleName().equals(info.type))) {
                    do {
                        info.sequence = "int" + intIndex++;
                    } while (sequenceSet.contains(info.sequence));
                } else {
                    do {
                        info.sequence = "str" + strIndex++;
                    } while (sequenceSet.contains(info.sequence));
                }
            }
        }

        return list;
    }

    private String getEnumDescription(Class<?> type, String description) {
        HashMap<String, HashMap<String, String>> enumName$lo_desc = new HashMap<String, HashMap<String, String>>();
        HashSet<String> loSet = new HashSet<String>();
        Pattern regex = Pattern.compile("\\n\\s{0,4}([a-zA-Z]{2}-[a-zA-Z]{2}):([^\\n]+)", Pattern.CASE_INSENSITIVE);
        HashMap<String, String> descMap = new HashMap<String, String>();
        Matcher m = regex.matcher("\nzh-cn:" + description);
        while (m.find()) {
            descMap.put(m.group(1).toLowerCase(), m.group(2));
        }
        for (Field ef : type.getDeclaredFields()) {
            if (ApiManager.isConstField(ef) && ef.getType() == type) {
                Description efAnnotation = ef.getAnnotation(Description.class);
                if (efAnnotation != null && efAnnotation.value() != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    enumName$lo_desc.put(ef.getName(), map);
                    String source = "\nzh-cn:" + efAnnotation.value();
                    Matcher matcher = regex.matcher(source);
                    while (matcher.find()) {
                        map.put(matcher.group(1).toLowerCase(), matcher.group(2));
                        loSet.add(matcher.group(1).toLowerCase());
                    }
                }
            }
        }
        String[] fieldArray = new String[enumName$lo_desc.size()];
        enumName$lo_desc.keySet().toArray(fieldArray);
        Arrays.sort(fieldArray, StringUtil.StringComparator);
        StringBuilder sb = new StringBuilder();
        if (loSet.contains("zh-cn")) {
            if (descMap.containsKey("zh-cn")) {
                sb.append(descMap.get("zh-cn"));
                sb.append(" ");
            }
            for (String name : fieldArray) {
                sb.append(name);
                sb.append(" ");
                sb.append(enumName$lo_desc.get(name).get("zh-cn"));
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            loSet.remove("zh-cn");
        }
        for (String lo : loSet) {
            sb.append("\n");
            sb.append(lo);
            sb.append(":");
            if (descMap.containsKey(lo)) {
                sb.append(descMap.get(lo));
                sb.append(" ");
            }
            for (String name : fieldArray) {
                sb.append(name);
                sb.append(" ");
                sb.append(enumName$lo_desc.get(name).get(lo));
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }

    private TypeStruct getRespStruct(String entityName, String groupName, Class<?> clazz) {
        if (respStructs.containsKey(entityName)) {
            return respStructs.get(entityName);
        } else {
            TypeStruct struct = buildTypeStruct(groupName, clazz);
            respStructs.put(entityName, struct);
            return struct;
        }
    }

    private TypeStruct getReqStruct(String entityName, String groupName, Class<?> clazz) {
        if (reqStructs.containsKey(entityName)) {
            return reqStructs.get(entityName);
        } else {
            TypeStruct struct = buildTypeStruct(groupName, clazz);
            reqStructs.put(entityName, struct);
            return struct;
        }
    }

    /**
     * 获取虚构的类Api_XXX_Array{List<XXX> value;}的结构信息
     */
    private TypeStruct getVirtualListRespStruct(String entityName, String groupName, Class<?> clazz) {
        if (vitualListStructs.containsKey(entityName)) {
            return vitualListStructs.get(entityName);
        } else {
            TypeStruct struct = buildVirtualListStruct(groupName, clazz);
            vitualListStructs.put(entityName, struct);
            return struct;
        }
    }

    /**
     * 对于接口返回为Collection或者数组xxx[],这里构建一个虚构的类Api_XXX_Array{List<XXX> value;}
     *
     * @return 虚构的类型结构
     */
    private TypeStruct buildVirtualListStruct(String groupName, Class<?> clazz) {
        TypeStruct r = new TypeStruct();
        r.fieldList = new ArrayList<FieldInfo>(1);
        if (!clazz.getName().startsWith("net.pocrd.")) {
            r.groupName = groupName;
        }
        r.name = getEntityName4CollectionAndArray(groupName, clazz);
        FieldInfo fi = new FieldInfo();
        fi.name = "value";
        fi.isList = true;
        Description desc = clazz.getAnnotation(Description.class);
        if (desc == null) {
            throw new RuntimeException(String.format("return type miss annotation Description,groupName:%s,type:%s", groupName, clazz.getName()));
        } else {
            fi.desc = clazz.getAnnotation(Description.class).value();
            fi.type = getEntityName(groupName, clazz);
        }
        r.fieldList.add(fi);
        return r;
    }

    /**
     * 构造请求结构体,注意在处理基本类型以及string的时候不能和入参相同
     */
    private TypeStruct buildTypeStruct(String groupName, Class<?> clazz) {
        try {
            TypeStruct r = new TypeStruct();
            r.fieldList = new LinkedList<FieldInfo>();
            if (!clazz.getName().startsWith("net.pocrd.")) {
                r.groupName = groupName;
            }
            r.name = getEntityName(groupName, clazz);
            for (Field f : clazz.getDeclaredFields()) {
                int modifier = f.getModifiers();
                if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                    continue;
                }
                FieldInfo fi = new FieldInfo();
                fi.name = f.getName();
                fi.isList = Collection.class.isAssignableFrom(f.getType()) || f.getType().isArray();
                Description d = f.getAnnotation(Description.class);
                if (d == null || d.value() == null || d.value().length() == 0) {
                    throw new RuntimeException("missing description " + groupName + " " + clazz.getName() + " " + f.getName());
                } else {
                    fi.desc = d.value();
                }

                Class<?> type = f.getType();
                EnumDef ed = f.getAnnotation(EnumDef.class);
                if (type.isPrimitive() || type == String.class || type.isEnum()) {
                    if (type.isEnum()) {
                        fi.desc = getEnumDescription(type, fi.desc);
                        fi.type = "string";
                        type = String.class;
                    } else if (type == String.class) {
                        fi.type = "string";
                        if (ed != null && ed.value() != null) {
                            fi.desc = getEnumDescription(ed.value(), fi.desc);
                        }
                    } else {
                        fi.type = type.getSimpleName();
                    }
                }
                if (fi.type != null) {
                    // do nothing
                } else if (type == String[].class) {
                    fi.type = "string";
                } else if (type == byte[].class) {
                    fi.type = byte.class.getSimpleName();
                } else if (type == boolean[].class) {
                    fi.type = boolean.class.getSimpleName();
                } else if (type == char[].class) {
                    fi.type = char.class.getSimpleName();
                } else if (type == short[].class) {
                    fi.type = short.class.getSimpleName();
                } else if (type == int[].class) {
                    fi.type = int.class.getSimpleName();
                } else if (type == float[].class) {
                    fi.type = float.class.getSimpleName();
                } else if (type == long[].class) {
                    fi.type = long.class.getSimpleName();
                } else if (type == double[].class) {
                    fi.type = double.class.getSimpleName();
                } else if (Collection.class.isAssignableFrom(type) || type.isArray() || isComplexType(type)) {
                    if (Collection.class.isAssignableFrom(type)) {
                        Type genericType;
                        try {
                            genericType = ((ParameterizedTypeImpl) f.getGenericType()).getActualTypeArguments()[0];
                        } catch (Throwable throwable) {
                            throw new RuntimeException("can not get generic type of list in " + groupName + " " + clazz.getName() + " " + f.getName(),
                                    throwable);
                        }
                        try {
                            type = Class.forName(((Class) genericType).getName(), true, Thread.currentThread().getContextClassLoader());
                            if (String.class == type) {
                                fi.type = "string";
                                if (ed != null && ed.value() != null) {
                                    fi.desc = getEnumDescription(ed.value(), fi.desc);
                                }
                            } else if (type.isEnum()) {
                                fi.desc = getEnumDescription(type, fi.desc);
                                fi.type = "string";
                            } else {
                                fi.type = getEntityName(groupName, type);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("generic type unsupported:" + genericType + " in " + clazz.getName(), e);
                        }
                    } else if (type.isArray()) {
                        if (type.getComponentType().isEnum()) {
                            fi.desc = getEnumDescription(type.getComponentType(), fi.desc);
                            fi.type = "string";
                        } else if (type.getComponentType() == String.class) {
                            fi.type = "string";
                            if (ed != null && ed.value() != null) {
                                fi.desc = getEnumDescription(ed.value(), fi.desc);
                            }
                        } else {
                            fi.type = getEntityName(groupName, type.getComponentType());
                        }
                    } else {
                        fi.type = getEntityName(groupName, type);
                    }
                }
                r.fieldList.add(fi);
            }
            return r;
        } catch (Exception e) {
            throw new RuntimeException("build type info failed. " + groupName + " " + clazz.getName(), e);
        }
    }

    private void fillTypeDependence(Class<?> clazz, HashSet<Class<?>> cs) {
        if (clazz.isPrimitive() || clazz == String.class || clazz.isEnum()) {
            return;
        }
        if (!hasPublicFields(clazz)) {//递归检查
            throw new RuntimeException("get public field failed, type dependence link failed, type:" + clazz.getName());
        }
        for (Field f : clazz.getDeclaredFields()) {
            Class<?> type = f.getType();
            int modifier = f.getModifiers();
            if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                continue;
            }
            if (Collection.class.isAssignableFrom(type)) {
                Type genericType;
                try {
                    genericType = ((ParameterizedTypeImpl) f.getGenericType()).getActualTypeArguments()[0];
                } catch (Throwable throwable) {
                    throw new RuntimeException("can not get generic type of list in " + clazz.getName(), throwable);
                }
                try {
                    type = Class.forName(((Class) genericType).getName(), true, Thread.currentThread().getContextClassLoader());
                } catch (Exception e) {
                    throw new RuntimeException("generic type unsupported:" + genericType + " in " + clazz.getName(), e);
                }
            } else if (type.isArray()) {
                if (type == byte[].class) {
                    type = byte.class;
                } else if (type == boolean[].class) {
                    type = boolean.class;
                } else if (type == short[].class) {
                    type = short.class;
                } else if (type == char[].class) {
                    type = char.class;
                } else if (type == int[].class) {
                    type = int.class;
                } else if (type == long[].class) {
                    type = long.class;
                } else if (type == float[].class) {
                    type = float.class;
                } else if (type == double[].class) {
                    type = double.class;
                } else {
                    throw new RuntimeException("array type unsupported:" + type.getName() + " in " + clazz.getName());
                }
            }
            if (type.isPrimitive() || type == String.class || type.isEnum()) {
                continue;
            }
            if (!cs.contains(type) && isComplexType(type)) {
                cs.add(type);
                fillTypeDependence(type, cs);
            }
        }
    }

    private void fillReqTypeDependence(Class<?> clazz, HashSet<Class<?>> cs) {
        if (clazz.isPrimitive() || clazz == String.class || clazz.isEnum()) {
            return;
        }
        if (!hasPublicFields(clazz)) {//递归检查
            throw new RuntimeException("get public field failed, type dependence link failed, type:" + clazz.getName());
        }
        for (Field f : clazz.getDeclaredFields()) {
            Class<?> type = f.getType();
            int modifier = f.getModifiers();
            if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                continue;
            }
            if (Collection.class.isAssignableFrom(type)) {
                Type genericType;
                try {
                    genericType = ((ParameterizedTypeImpl) f.getGenericType()).getActualTypeArguments()[0];
                } catch (Throwable throwable) {
                    throw new RuntimeException("can not get generic type of list in " + clazz.getName(), throwable);
                }
                try {
                    type = Class.forName(((Class) genericType).getName(), true, Thread.currentThread().getContextClassLoader());
                } catch (Exception e) {
                    throw new RuntimeException("generic type unsupported:" + genericType + " in " + clazz.getName(), e);
                }
            } else if (type.isArray()) {
                type = type.getComponentType();
            }
            if (type.isPrimitive() || type == String.class || type.isEnum()) {
                continue;
            }
            if (!cs.contains(type) && isComplexType(type)) {
                cs.add(type);
                fillReqTypeDependence(type, cs);
            }
        }
    }

    /**
     * 判断是否为可序列化输出的复合类型
     */
    private boolean isComplexType(Class<?> type) {
        if (type.isPrimitive() || type == String.class || type.isEnum()) {
            return false;
        }
        Description an = type.getAnnotation(Description.class);
        if (an == null) {
            throw new RuntimeException("type unsupported: in " + type.getName());
        }
        return true;
    }

    /**
     * 获取系统级参数
     */
    private List<SystemParameterInfo> getSysParamInfo() {
        Field[] fields = CommonParameter.class.getDeclaredFields();
        List<SystemParameterInfo> systemParameterInfos = new LinkedList<SystemParameterInfo>();
        for (Field field : fields) {
            if (ApiManager.isConstField(field)) {
                Description desc = field.getAnnotation(Description.class);
                if (desc != null) {
                    SystemParameterInfo systemParameterInfo = new SystemParameterInfo();
                    try {
                        systemParameterInfo.name = (String) field.get(CommonParameter.class);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("get const field failed", e);
                    }
                    systemParameterInfo.desc = desc.value();
                    systemParameterInfos.add(systemParameterInfo);
                } else {
                    throw new RuntimeException(String.format("miss description of field in CommonParameter, field name:%s", field.getName()));
                }
            }
        }
        return systemParameterInfos;
    }

    private String getEntityName(String group, Class<?> type) {
        return (group == null || group.length() == 0 || type.getName().startsWith(
                "net.pocrd.")) ? "Api_" + type.getSimpleName() : "Api_" + group.toUpperCase() + "_" + type.getSimpleName();
    }

    private String getEntityName4CollectionAndArray(String group, Class<?> type) {
        return (group == null || group.length() == 0 || type.getName().startsWith(
                "net.pocrd.")) ?
                "Api_" + type.getSimpleName() + "_ArrayResp" :
                "Api_" + group.toUpperCase() + "_" + type.getSimpleName() + "_ArrayResp";
    }

    private boolean hasPublicFields(Class<?> clazz) {
        if (clazz == null) {
            throw new RuntimeException("get public fields num failed, clazz is null!");
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int modifier = field.getModifiers();
            if (Modifier.isPublic(modifier)) {
                return true;
            }
        }
        return false;
    }
}
