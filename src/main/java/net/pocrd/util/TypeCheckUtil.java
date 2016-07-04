package net.pocrd.util;

import net.pocrd.annotation.Description;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;

public class TypeCheckUtil {
    /**
     * 支持的基本类型,当递归遇到这些类型进行回溯
     */
    private final static HashSet<Class<?>> RETURNTYPE_ACCEPT_CLAZZ_SET = new HashSet<Class<?>>() {
        {
            add(String.class);
            add(String[].class);
            add(boolean.class);
            add(boolean[].class);
            add(byte.class);
            add(byte[].class);
            add(short.class);
            add(short[].class);
            add(char.class);
            add(char[].class);
            add(int.class);
            add(int[].class);
            add(long.class);
            add(long[].class);
            add(double.class);
            add(double[].class);
            add(float.class);
            add(float[].class);
            // RawString在输出的时候会被解释为String
            add(RawString.class);
        }
    };

    /**
     * 支持的入参基本类型
     */
    private final static HashSet<Class<?>> INPUT_ACCEPT_CLAZZ_SET = new HashSet<Class<?>>() {
        {
            add(String.class);
            add(boolean.class);
            add(byte.class);
            add(short.class);
            add(char.class);
            add(int.class);
            add(long.class);
            add(double.class);
            add(float.class);
        }
    };

    /**
     * Type检查器
     */
    public interface TypeChecker {
        boolean accept(Class<?> type, String desc);
    }

    /**
     * 检查返回类型是否添加了Description注解
     */
    public static class DescriptionAnnotationChecker implements TypeChecker {
        @Override
        public boolean accept(Class<?> type, String desc) {
            if (INPUT_ACCEPT_CLAZZ_SET.contains(type) || type.isEnum()) {
                return true;
            }
            Description description = type.getAnnotation(Description.class);
            if (description == null) {
                throw new RuntimeException("description missing, " + type.getName() + " in " + desc);
            }

            return true;
        }
    }

    /**
     * public field检查,public field不能为空
     */
    public static class PublicFieldChecker implements TypeChecker {
        @Override
        public boolean accept(Class<?> returnType, String desc) {
            boolean hasPublicField = false;
            Field[] fields = returnType.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    if (Modifier.isPublic(field.getModifiers())) {
                        hasPublicField = true;
                        break;
                    }
                }
            }
            if (!hasPublicField) {
                throw new RuntimeException("no public field is defined in " + returnType + " of " + desc);
            }
            boolean isAbstractOrIsInterface = false;
            int modified = returnType.getModifiers();
            if (Modifier.isAbstract(modified) || Modifier.isInterface(modified)) {
                if (Collection.class.isAssignableFrom(returnType)) {
                    isAbstractOrIsInterface = false;
                } else {
                    isAbstractOrIsInterface = true;
                }
            }
            fields = returnType.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
                        if (Collection.class.isAssignableFrom(returnType)) {
                            isAbstractOrIsInterface = false;
                        } else {
                            isAbstractOrIsInterface = true;
                        }
                    }
                }
            }
            if (isAbstractOrIsInterface) {
                throw new RuntimeException("do not allow abstract or interface defined in " + returnType + " of " + desc);
            }
            return true;
        }
    }

    /**
     * 检查返回类型是否实现了Serialiable接口
     */
    public static class SerializableImplChecker implements TypeChecker {
        @Override
        public boolean accept(Class<?> returnType, String desc) {
            if (!Serializable.class.isAssignableFrom(returnType)) {
                throw new RuntimeException(
                        "serializable miss,return type must implements Serializable, " + returnType.getName() + " in " + desc);
            }
            return true;
        }
    }

    private static ThreadLocal<WeakReference<HashSet<Class<?>>>> parsedInputTypes  = new ThreadLocal<WeakReference<HashSet<Class<?>>>>();
    private static ThreadLocal<WeakReference<HashSet<Class<?>>>> parsedOutputTypes = new ThreadLocal<WeakReference<HashSet<Class<?>>>>();

    /**
     * 在启动时对每个api的返回结果进行递归的检查
     *
     * @param serviceInterfaceName dubbo接口名
     * @param returnType           返回类型
     * @param actuallyGenericType  实际泛型类类型,当returnType implements Collection
     * @param checkers             returnType检查器
     */
    public static void recursiveCheckReturnType(final String serviceInterfaceName, Class<?> returnType, Class<?> actuallyGenericType,
            final TypeChecker... checkers) {
        if (!RETURNTYPE_ACCEPT_CLAZZ_SET.contains(returnType) && !returnType.isEnum()) {
            WeakReference<HashSet<Class<?>>> wr = parsedOutputTypes.get();
            HashSet<Class<?>> types = null;
            if (wr != null) {
                types = wr.get();
            }
            if (types == null) {
                types = new HashSet<Class<?>>();
                parsedOutputTypes.set(new WeakReference<HashSet<Class<?>>>(types));
            }
            if (types.contains(returnType)) {
                return;
            } else {
                types.add(returnType);
            }

            if (returnType.isArray()) {
                throw new RuntimeException("unsupport array type, " + returnType.getName() + " in " + serviceInterfaceName);
            } else if (Collection.class.isAssignableFrom(returnType)) {
                if (actuallyGenericType == null) {
                    throw new RuntimeException("miss actually generic type, " + returnType.getName() + " in " + serviceInterfaceName);
                }
                recursiveCheckReturnType(serviceInterfaceName, actuallyGenericType, null, checkers);
            } else {
                if (checkers != null) {
                    for (TypeChecker checker : checkers) {
                        if (!checker.accept(returnType, serviceInterfaceName)) {
                            break;
                        }
                    }
                }
                Field[] fields = returnType.getDeclaredFields();
                if (fields != null) {
                    for (Field field : fields) {
                        Class<?> fieldActualClazz = null;
                        int modifier = field.getModifiers();
                        if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                            continue;
                        }
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            Type fieldActuallyGenericType;
                            try {
                                fieldActuallyGenericType = ((ParameterizedTypeImpl)field.getGenericType()).getActualTypeArguments()[0];
                            } catch (Exception exception) {
                                throw new RuntimeException("can not get generic type, " + returnType + " in " + serviceInterfaceName, exception);
                            }
                            try {
                                fieldActualClazz = Class.forName(((Class<?>)fieldActuallyGenericType).getName(), true, Thread.currentThread().getContextClassLoader());
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException("generic type unsupported, " + fieldActuallyGenericType + " in " + serviceInterfaceName,
                                        e);
                            }
                        }
                        recursiveCheckReturnType(serviceInterfaceName, field.getType(), fieldActualClazz, checkers);
                    }
                }
            }
        }
    }

    /**
     * 在启动时对每个api的入参进行递归检查
     *
     * @param serviceInterfaceName dubbo接口名
     * @param inputType            返回类型
     * @param actuallyGenericType  实际泛型类类型,当inputType implements Collection
     * @param checkers             returnType检查器
     */
    public static void recursiveCheckInputType(final String serviceInterfaceName, Class<?> inputType, Class<?> actuallyGenericType,
            final TypeChecker... checkers) {
        if (!INPUT_ACCEPT_CLAZZ_SET.contains(inputType) && !inputType.isEnum()) {
            WeakReference<HashSet<Class<?>>> wr = parsedInputTypes.get();
            HashSet<Class<?>> types = null;
            if (wr != null) {
                types = wr.get();
            }
            if (types == null) {
                types = new HashSet<Class<?>>();
                parsedInputTypes.set(new WeakReference<HashSet<Class<?>>>(types));
            }
            if (types.contains(inputType)) {
                if (actuallyGenericType == null || types.contains(actuallyGenericType)) {
                    // 当 actuallyGenericType 不为空的时候，只有两个类都已经被解析过，才会被跳过本次检查
                    return;
                } else {
                    types.add(inputType);
                    types.add(actuallyGenericType);
                }
            } else {
                types.add(inputType);
                if (actuallyGenericType != null) {
                    types.add(actuallyGenericType);
                }
            }
            if (inputType.isArray()) {
                recursiveCheckInputType(serviceInterfaceName, inputType.getComponentType(), null, checkers);
                return;
            } else if (Collection.class.isAssignableFrom(inputType)) {
                if (actuallyGenericType == null) {
                    throw new RuntimeException("miss actually generic type, " + inputType.getName() + " in " + serviceInterfaceName);
                }
                inputType = actuallyGenericType;
            }
            if (checkers != null) {
                for (TypeChecker checker : checkers) {
                    if (!checker.accept(inputType, serviceInterfaceName)) {
                        break;
                    }
                }
            }
            Field[] fields = inputType.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    Class<?> fieldActualClazz = null;
                    int modifier = field.getModifiers();
                    if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                        continue;
                    }
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Type fieldActuallyGenericType = null;
                        try {
                            fieldActuallyGenericType = ((ParameterizedTypeImpl)field.getGenericType()).getActualTypeArguments()[0];
                        } catch (Exception exception) {
                            throw new RuntimeException("can not get generic type, " + inputType + " in " + serviceInterfaceName, exception);
                        }
                        try {
                            fieldActualClazz = Class.forName(((Class)fieldActuallyGenericType).getName(), true, Thread.currentThread().getContextClassLoader());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("generic type unsupported, " + fieldActuallyGenericType + " in " + serviceInterfaceName,
                                    e);
                        }
                    }
                    recursiveCheckInputType(serviceInterfaceName, field.getType(), fieldActualClazz, checkers);
                }
            }
        }
    }
}
