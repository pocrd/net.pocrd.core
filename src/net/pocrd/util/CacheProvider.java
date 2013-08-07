package net.pocrd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import net.pocrd.annotation.CacheMethod;
import net.pocrd.annotation.CacheParameter;
import net.pocrd.annotation.CacheParameter.CacheKeyType;
import net.pocrd.core.PocClassLoader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Generate subclass with cacheMethod,then create and cache an single instance for input class.
 * 
 * @author guankaiqiang
 * @param <T>
 */
public class CacheProvider implements Opcodes {

    private static ConcurrentHashMap<Class<?>, Object> cache         = new ConcurrentHashMap<Class<?>, Object>();
    private final static String                        CACHE_SPLITER = "|";

    @SuppressWarnings("unchecked")
    public static <T> T getSingleton(Class<T> clazz) {
        T instance = (T)cache.get(clazz);
        if (instance == null) {
            synchronized (cache) {
                instance = (T)cache.get(clazz);
                if (instance == null) {
                    instance = createSingleton(clazz);
                    cache.put(clazz, instance);
                }
            }
        }
        return instance;
    }

    private static <T> T createSingleton(Class<T> clazz) {
        try {
            if (hasCacheMethod(clazz)) {
                return newCachedClassInstance(clazz);
            } else {
                return SingletonUtil.getSingleton(clazz);
            }
        } catch (Exception e) {
            throw new RuntimeException("单例创建失败", e);
        }
    }

    /**
     * @author guankaiqiang
     * @param clazz CacheProvider generates cachedClass, only if target has at least one or more method which can be accessed publicly and its return
     *            type is not void and is not final,not abstract or not static
     */
    public static <T> boolean hasCacheMethod(Class<T> clazz) {
        boolean isValid = false;
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            isValid |= checkCacheMethod(m);
        }
        return isValid;
    }

    /**
     * 检测方法是符合被代理的条件
     * 
     * @param method
     */
    private static boolean checkCacheMethod(Method method) {
        CacheMethod cacheAnnotation = method.getAnnotation(CacheMethod.class);
        if (cacheAnnotation != null && cacheAnnotation.enable()) {
            Class<?> returnType = method.getReturnType();
            int mod = method.getModifiers();
            if (Modifier.isAbstract(mod)) throw new RuntimeException("Method can not be abstract,method name:" + method.getName());
            if (!Modifier.isPublic(mod)) throw new RuntimeException("Method must be public,method name:" + method.getName());
            if (Modifier.isFinal(mod)) throw new RuntimeException("Method can not be final,method name:" + method.getName());
            if (Modifier.isStatic(mod)) throw new RuntimeException("Method can not be static,method name:" + method.getName());
            if ("void".equals(returnType.getName())) {
                if (CommonConfig.isDebug)// 空方法可以跳过代理
                    throw new RuntimeException("Method return type can not be void,method name:" + method.getName());
                else return false;
            }
            return true;
        } else return false;
    }

    /**
     * 生成缓存Instatnce
     * 
     * @author guankaiqiang
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T newCachedClassInstance(Class<T> clazz) {
        try {
            String className = "net/pocrd/autogen/Cache_" + clazz.getSimpleName();
            String superClassName = clazz.getName().replace('.', '/');
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            MethodVisitorWrapper mvWrapper;
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, superClassName, null);
            cw.visitSource("Cache_" + clazz.getSimpleName() + ".java", null);
            {
                // init
                mvWrapper = new MethodVisitorWrapper(ASM4, cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null));
                mvWrapper.declareArgs(false, null);
                mvWrapper.visitCode();
                Label l0 = new Label();
                mvWrapper.visitLabel(l0);
                mvWrapper.loadArg(0);
                mvWrapper.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
                mvWrapper.visitInsn(RETURN);
                Label l1 = new Label();
                mvWrapper.visitLabel(l1);
                mvWrapper.visitLocalVariable("this", "L" + className + ";", null, l0, l1, 0);
                mvWrapper.visitMaxs(1, 1);
                mvWrapper.visitEnd();
            }
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                CacheMethod cacheAnnotation = m.getAnnotation(CacheMethod.class);
                if (cacheAnnotation != null && cacheAnnotation.enable()) {
                    Class<?> returnType = m.getReturnType();
                    if ("void".equals(returnType.getName())) continue;
                    String keyName = CommonConfig.Instance.cacheVersion + CACHE_SPLITER + cacheAnnotation.key() + CACHE_SPLITER
                            + returnType.getCanonicalName() + CACHE_SPLITER;
                    int expire = cacheAnnotation.expire();
                    Class<?>[] paramTypes = m.getParameterTypes();
                    mvWrapper = new MethodVisitorWrapper(ASM4, cw.visitMethod(ACC_PUBLIC, m.getName(), Type.getMethodDescriptor(m), null, null));
                    mvWrapper.declareArgs(Modifier.isStatic(m.getModifiers()), paramTypes);
                    LocalBuilder cacheManagerBuilder = mvWrapper.declareLocal(CacheManager.class);
                    LocalBuilder returnTypeBuilder = mvWrapper.declareLocal(returnType);
                    LocalBuilder cacheKeyBuilder = mvWrapper.declareLocal(String.class);
                    LocalBuilder cacheObjectBuilder = mvWrapper.declareLocal(Object.class);
                    Label ljump0 = new Label();
                    Label ljump1 = new Label();
                    Label ljump2 = new Label();
                    mvWrapper.visitCode();
                    // 1.generate cachekey
                    {
                        mvWrapper.visitTypeInsn(NEW, "java/lang/StringBuilder");
                        mvWrapper.visitInsn(DUP);
                        mvWrapper.visitLdcInsn(keyName);
                        mvWrapper.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                        Annotation[][] paramAnnotations = m.getParameterAnnotations();
                        if (paramAnnotations != null && paramAnnotations.length != 0) {
                            if (CommonConfig.isDebug) {
                                if (paramTypes.length != paramAnnotations.length)
                                    throw new RuntimeException("存在尚未标记CacheParameter的入参，" + m.getName());
                            }
                            int indexOfParam = 0;
                            for (Annotation[] annotations : paramAnnotations) {
                                if (annotations != null && annotations.length != 0) {
                                    for (Annotation annotation : annotations) {
                                        if (annotation.annotationType() == CacheParameter.class) {
                                            CacheParameter paramAnnotation = (CacheParameter)annotation;
                                            if (paramAnnotation.type() == CacheKeyType.Normal) {
                                                Class<?> paramType = paramTypes[indexOfParam];
                                                String paramDes = "";
                                                mvWrapper.loadArg(indexOfParam + 1);
                                                if (paramType.isArray()) {
                                                    paramDes = StringHelper.checkCast(Type.getDescriptor(paramType)) ? Type.getDescriptor(paramType)
                                                            : Type.getDescriptor(Object[].class);// 隐式类型转换
                                                    mvWrapper.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/StringHelper", "toString", "(" + paramDes
                                                            + ")" + Type.getDescriptor(String.class));
                                                    mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                                            "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                                                } else {
                                                    if (paramType.isPrimitive()) {
                                                        String ptype = paramType.getName();
                                                        if ("int".equals(ptype) || "short".equals(ptype) || "byte".equals(ptype))
                                                            paramDes = "I";// 隐式类型转换
                                                        else paramDes = Type.getDescriptor(paramType);
                                                    } else {
                                                        paramDes = Type.getDescriptor(Object.class);// 隐式类型转换
                                                    }
                                                    mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + paramDes
                                                            + ")" + Type.getDescriptor(StringBuilder.class));
                                                }
                                                mvWrapper.visitLdcInsn(CACHE_SPLITER);
                                                mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                                            } else {
                                                // TODO:Support
                                                // autopaging/filter
                                                throw new RuntimeException("不识别的CacheKeyType:" + paramAnnotation.type());
                                            }
                                        }
                                    }
                                    indexOfParam++;
                                }
                            }
                        }
                        mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                        mvWrapper.setLocal(cacheKeyBuilder);
                    }
                    if (CommonConfig.isDebug) {
                        mvWrapper.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mvWrapper.loadLocal(cacheKeyBuilder);
                        mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
                    }
                    {
                        // 2.CacheManager cacheManager = CacheManager.getSingleton();
                        mvWrapper.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/CacheManager", "getSingleton", "()Lnet/pocrd/util/CacheManager;");
                        mvWrapper.setLocal(cacheManagerBuilder);
                    }
                    {
                        // 3.Object obj = cacheManager.get(cachekey);
                        mvWrapper.loadLocal(cacheManagerBuilder);
                        mvWrapper.loadLocal(cacheKeyBuilder);
                        mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "net/pocrd/util/CacheManager", "get", "(Ljava/lang/String;)Ljava/lang/Object;");
                        mvWrapper.setLocal(cacheObjectBuilder);
                    }
                    {
                        // 4.if(obj==null)
                        mvWrapper.loadLocal(cacheObjectBuilder);
                        mvWrapper.visitJumpInsn(IFNONNULL, ljump0);
                    }
                    {
                        // 5.DemoEntity demo=super.getDemoEntity();
                        for (int i = 0; i <= m.getParameterTypes().length; i++) {
                            mvWrapper.loadArg(i);// start from this
                        }
                        mvWrapper.visitMethodInsn(INVOKESPECIAL, superClassName, m.getName(), Type.getMethodDescriptor(m));
                        mvWrapper.setLocal(returnTypeBuilder);
                    }
                    {
                        // 6.if(demo!=null)
                        if (!returnType.isPrimitive()) {
                            mvWrapper.loadLocal(returnTypeBuilder);
                            mvWrapper.visitJumpInsn(IFNULL, ljump1);
                        }
                    }
                    {
                        // 7.cacheManager.set(cachekey,demoEntity,expire);
                        mvWrapper.loadLocal(cacheManagerBuilder);
                        mvWrapper.loadLocal(cacheKeyBuilder);
                        mvWrapper.loadLocal(returnTypeBuilder);
                        if (returnType.isPrimitive()) mvWrapper.doInbox(returnType);// inbox
                        mvWrapper.visitIntInsn(BIPUSH, expire);
                        mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "net/pocrd/util/CacheManager", "set", "(Ljava/lang/String;Ljava/lang/Object;I)Z");
                        mvWrapper.visitInsn(POP);
                        mvWrapper.loadLocal(returnTypeBuilder);
                        mvWrapper.doReturn(returnType);
                    }
                    {
                        // 8.return null;
                        if (!returnType.isPrimitive()) {
                            mvWrapper.visitLabel(ljump1);
                            mvWrapper.visitInsn(ACONST_NULL);
                            mvWrapper.doReturn(returnType);
                        }
                    }
                    {
                        // if (obj instanceof Integer)
                        // return ((Integer) obj).intValue();
                        {
                            mvWrapper.visitLabel(ljump0);
                            mvWrapper.loadLocal(cacheObjectBuilder);
                            mvWrapper.doInstanceof(returnType);
                            mvWrapper.visitJumpInsn(IFEQ, ljump2);
                            mvWrapper.loadLocal(cacheObjectBuilder);
                            mvWrapper.doCast(returnType);
                            mvWrapper.doReturn(returnType);
                        }
                        {
                            // else throw new RuntimeException(...);
                            mvWrapper.visitLabel(ljump2);
                            mvWrapper.visitTypeInsn(NEW, "java/lang/RuntimeException");
                            mvWrapper.visitInsn(DUP);
                            mvWrapper.visitTypeInsn(NEW, "java/lang/StringBuilder");
                            mvWrapper.visitInsn(DUP);
                            mvWrapper.visitLdcInsn("Cache object conflict,key:");
                            mvWrapper.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                            mvWrapper.loadLocal(cacheKeyBuilder);
                            mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                            mvWrapper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                            mvWrapper.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V");
                            mvWrapper.visitInsn(ATHROW);
                        }
                    }
                    mvWrapper.visitMaxs(0, 0);
                    mvWrapper.visitEnd();
                }
            }
            cw.visitEnd();
            if (CommonConfig.isDebug) {
                outPutClassFile("Cache_" + clazz.getSimpleName(), cw.toByteArray());
            }
            T e = (T)new PocClassLoader(Thread.currentThread().getContextClassLoader()).defineClass(className.replace('/', '.'), cw.toByteArray())
                    .newInstance();
            return e;
        } catch (Exception e) {
            throw new RuntimeException("generate failed. " + clazz.getName(), e);
        }
    }

    private static void outPutClassFile(String fileName, byte[] byteArray) {
        FileOutputStream fos = null;
        try {
            File folder = new File(CommonConfig.Instance.autogenPath + "\\CachedClass\\");
            if (!folder.exists()) folder.mkdirs();
            fos = new FileOutputStream(CommonConfig.Instance.autogenPath + "\\CachedClass\\" + fileName + ".class");
            fos.write(byteArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
