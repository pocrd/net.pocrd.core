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
import net.pocrd.util.CommonConfig.CacheDBType;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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
     * 根据配置实例化不同的缓存
     * 
     * @param mv
     */
    private static void getCacheManagerInstance(MethodVisitor mv) {
        if (CommonConfig.Instance.cacheType.equals(CacheDBType.Redis)) {
            mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/CacheManager4Redis", "getSingleton", "()Lnet/pocrd/util/ICacheManager;");
        } else if (CommonConfig.Instance.cacheType.equals(CacheDBType.Memcache)) {
            mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/CacheManager4Memcache", "getSingleton", "()Lnet/pocrd/util/ICacheManager;");
        } else throw new RuntimeException("不支持的CacheDBType");
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
            MethodVisitorHelper mvHelper;
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, superClassName, null);
            cw.visitSource("Cache_" + clazz.getSimpleName() + ".java", null);
            {
                // init
                mvHelper = new MethodVisitorHelper(ASM4, cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null));
                mvHelper.declareArgs(false, null);
                mvHelper.visitCode();
                Label l0 = new Label();
                mvHelper.visitLabel(l0);
                mvHelper.loadArg(0);
                mvHelper.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
                mvHelper.visitInsn(RETURN);
                Label l1 = new Label();
                mvHelper.visitLabel(l1);
                mvHelper.visitLocalVariable("this", "L"+className+";", null, l0, l1, 0);
                mvHelper.visitMaxs(1, 1);
                mvHelper.visitEnd();
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
                    mvHelper = new MethodVisitorHelper(ASM4, cw.visitMethod(ACC_PUBLIC, m.getName(), Type.getMethodDescriptor(m), null, null));
                    mvHelper.declareArgs(Modifier.isStatic(m.getModifiers()), paramTypes);
                    LocalBuilder cacheManagerBuilder = mvHelper.declareLocal(ICacheManager.class);
                    LocalBuilder returnTypeBuilder = mvHelper.declareLocal(returnType);
                    LocalBuilder cacheKeyBuilder = mvHelper.declareLocal(String.class);
                    LocalBuilder cacheObjectBuilder = mvHelper.declareLocal(Object.class);
                    Label ljump0 = new Label();
                    Label ljump1 = new Label();
                    Label ljump2 = new Label();
                    mvHelper.visitCode();
                    // 1.generate cachekey
                    {
                        mvHelper.visitTypeInsn(NEW, "java/lang/StringBuilder");
                        mvHelper.visitInsn(DUP);
                        mvHelper.visitLdcInsn(keyName);
                        mvHelper.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
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
                                                mvHelper.loadArg(indexOfParam + 1);
                                                if (paramType.isArray()) {
                                                    paramDes = StringHelper.checkCast(Type.getDescriptor(paramType)) ? Type.getDescriptor(paramType)
                                                            : Type.getDescriptor(Object[].class);// 隐式类型转换
                                                    mvHelper.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/StringHelper", "toString", "(" + paramDes
                                                            + ")" + Type.getDescriptor(String.class));
                                                    mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
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
                                                    mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + paramDes + ")"
                                                            + Type.getDescriptor(StringBuilder.class));
                                                }
                                                mvHelper.visitLdcInsn(CACHE_SPLITER);
                                                mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
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
                        mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                        mvHelper.setLocal(cacheKeyBuilder);
                    }
                    if (CommonConfig.isDebug) {
                        mvHelper.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mvHelper.loadLocal(cacheKeyBuilder);
                        mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
                    }
                    {
                        // 2.ICacheManager cacheManager = CacheManager4Redis.getSingleton();
                        getCacheManagerInstance(mvHelper);
                        mvHelper.setLocal(cacheManagerBuilder);
                    }
                    {
                        // 3.Object obj = cacheManager.get(cachekey);
                        mvHelper.loadLocal(cacheManagerBuilder);
                        mvHelper.loadLocal(cacheKeyBuilder);
                        mvHelper.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/util/ICacheManager", "get", "(Ljava/lang/String;)Ljava/lang/Object;");
                        mvHelper.setLocal(cacheObjectBuilder);
                    }
                    {
                        // 4.if(obj==null)
                        mvHelper.loadLocal(cacheObjectBuilder);
                        mvHelper.visitJumpInsn(IFNONNULL, ljump0);
                    }
                    {
                        // 5.DemoEntity demo=super.getDemoEntity();
                        for (int i = 0; i <= m.getParameterTypes().length; i++) {
                            mvHelper.loadArg(i);// start from this
                        }
                        mvHelper.visitMethodInsn(INVOKESPECIAL, superClassName, m.getName(), Type.getMethodDescriptor(m));
                        mvHelper.setLocal(returnTypeBuilder);
                    }
                    {
                        // 6.if(demo!=null)
                        if (!returnType.isPrimitive()) {
                            mvHelper.loadLocal(returnTypeBuilder);
                            mvHelper.visitJumpInsn(IFNULL, ljump1);
                        }
                    }
                    {
                        // 7.cacheManager.set(cachekey,demoEntity,expire);
                        mvHelper.loadLocal(cacheManagerBuilder);
                        mvHelper.loadLocal(cacheKeyBuilder);
                        mvHelper.loadLocal(returnTypeBuilder);
                        if (returnType.isPrimitive()) BytecodeUtil.inbox(mvHelper, returnType);// inbox
                        mvHelper.visitIntInsn(BIPUSH, expire);
                        mvHelper.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/util/ICacheManager", "set", "(Ljava/lang/String;Ljava/lang/Object;I)Z");
                        mvHelper.visitInsn(POP);
                        mvHelper.loadLocal(returnTypeBuilder);
                        BytecodeUtil.doReturn(mvHelper, returnType);
                    }
                    {
                        // 8.return null;
                        if (!returnType.isPrimitive()) {
                            mvHelper.visitLabel(ljump1);
                            mvHelper.visitInsn(ACONST_NULL);
                            BytecodeUtil.doReturn(mvHelper, returnType);
                        }
                    }
                    {
                        // if (obj instanceof Integer)
                        // return ((Integer) obj).intValue();
                        {
                            mvHelper.visitLabel(ljump0);
                            mvHelper.loadLocal(cacheObjectBuilder);
                            BytecodeUtil.doInstanceof(mvHelper, returnType);
                            mvHelper.visitJumpInsn(IFEQ, ljump2);
                            mvHelper.loadLocal(cacheObjectBuilder);
                            BytecodeUtil.doCast(mvHelper, returnType);
                            BytecodeUtil.doReturn(mvHelper, returnType);
                        }
                        {
                            // else throw new RuntimeException(...);
                            mvHelper.visitLabel(ljump2);
                            mvHelper.visitTypeInsn(NEW, "java/lang/RuntimeException");
                            mvHelper.visitInsn(DUP);
                            mvHelper.visitTypeInsn(NEW, "java/lang/StringBuilder");
                            mvHelper.visitInsn(DUP);
                            mvHelper.visitLdcInsn("Cache object conflict,key:");
                            mvHelper.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                            mvHelper.loadLocal(cacheKeyBuilder);
                            mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                            mvHelper.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                            mvHelper.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V");
                            mvHelper.visitInsn(ATHROW);
                        }
                    }
                    mvHelper.visitMaxs(0, 0);
                    mvHelper.visitEnd();
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
