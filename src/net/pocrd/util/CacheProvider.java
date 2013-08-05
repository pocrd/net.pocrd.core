package net.pocrd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
     * @param clazz
     * @return
     */
    public static <T> boolean hasCacheMethod(Class<T> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            CacheMethod cacheAnnotation = m.getAnnotation(CacheMethod.class);
            if (cacheAnnotation != null && !"void".equals(m.getReturnType().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成缓存Class
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
            MethodVisitor mv;
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, superClassName, null);
            cw.visitSource("Cache_" + clazz.getSimpleName() + ".java", null);
            {
                // init
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", Type.getDescriptor(clazz), null, l0, l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                CacheMethod cacheAnnotation = m.getAnnotation(CacheMethod.class);
                if (cacheAnnotation != null && cacheAnnotation.enable()) {
                    Class<?> returnType = m.getReturnType();
                    if ("void".equals(returnType.getName())) {
                        continue;
                    }
                    String keyName = CommonConfig.Instance.cacheVersion + CACHE_SPLITER + cacheAnnotation.key() + CACHE_SPLITER
                            + returnType.getCanonicalName() + CACHE_SPLITER;
                    int expire = cacheAnnotation.expire();
                    Label ljump0 = new Label();
                    Label ljump1 = new Label();
                    Label ljump2 = new Label();
                    LocalVarTable varTb = new LocalVarTable(m);
                    {
                        mv = cw.visitMethod(ACC_PUBLIC, m.getName(), Type.getMethodDescriptor(m), null, null);
                        mv.visitCode();
                        // 1.generate cachekey
                        {
                            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                            mv.visitInsn(DUP);
                            mv.visitLdcInsn(keyName);
                            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                            Annotation[][] paramAnnotations = m.getParameterAnnotations();
                            Class<?>[] paramTypes = m.getParameterTypes();
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
                                                    varTb.loadArg(mv, indexOfParam + 1);
                                                    if (paramType.isArray()) {
                                                        paramDes = StringHelper.descriptorSet.contains(Type.getDescriptor(paramType)) ? Type
                                                                .getDescriptor(paramType) : Type.getDescriptor(Object[].class);// 隐式类型转换
                                                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/StringHelper", "toString", "(" + paramDes
                                                                + ")" + Type.getDescriptor(String.class));
                                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
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
                                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + paramDes + ")"
                                                                + Type.getDescriptor(StringBuilder.class));
                                                    }
                                                    mv.visitLdcInsn(CACHE_SPLITER);
                                                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
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
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                            varTb.setLocal(mv, String.class);
                        }
                        if (CommonConfig.isDebug) {
                            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                            varTb.loadLocal(mv, 0);
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
                        }
                        {
                            // 2.ICacheManager localCacheManager4Redis =
                            // CacheManager4Redis.getSingleton();
                            if (CommonConfig.Instance.cacheType.equals(CacheDBType.Redis)) {
                                mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/CacheManager4Redis", "getSingleton",
                                        "()Lnet/pocrd/util/CacheManager4Redis;");
                                varTb.setLocal(mv, CacheManager4Redis.class);
                            } else {
                                // TODO:Support memcache
                                throw new RuntimeException("不支持的CacheDBType");
                            }
                        }
                        {
                            // 3.Object obj = cacheManager.get(cachekey);
                            varTb.loadLocal(mv, 1);
                            varTb.loadLocal(mv, 0);
                            mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/util/ICacheManager", "get", "(Ljava/lang/String;)Ljava/lang/Object;");
                            varTb.setLocal(mv, ICacheManager.class);
                        }
                        {
                            // 4.if(obj==null)
                            varTb.loadLocal(mv, 2);
                            mv.visitJumpInsn(IFNONNULL, ljump0);
                        }
                        {
                            // 5.DemoEntity demo=super.getDemoEntity();
                            for (int i = 0; i <= m.getParameterTypes().length; i++) {
                                // start from this
                                varTb.loadArg(mv, i);
                            }
                            mv.visitMethodInsn(INVOKESPECIAL, superClassName, m.getName(), Type.getMethodDescriptor(m));
                            varTb.setLocal(mv, returnType);
                        }
                        {
                            // 6.if(demo!=null)
                            if (!returnType.isPrimitive()) {
                                varTb.loadLocal(mv, 3);
                                mv.visitJumpInsn(IFNULL, ljump1);
                            }
                        }
                        {
                            // 7.localCacheManager4Redis.set(cachekey,demoEntity,expire);
                            varTb.loadLocal(mv, 1);
                            varTb.loadLocal(mv, 0);
                            varTb.loadLocal(mv, 3);
                            // inbox
                            if (returnType.isPrimitive()) BytecodeUtil.inbox(mv, returnType);
                            mv.visitIntInsn(BIPUSH, expire);
                            mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/util/ICacheManager", "set", "(Ljava/lang/String;Ljava/lang/Object;I)Z");
                            mv.visitInsn(POP);
                            varTb.loadLocal(mv, 3);
                            BytecodeUtil.doReturn(mv, returnType);
                        }
                        {
                            // 8.return null;
                            if (!returnType.isPrimitive()) {
                                mv.visitLabel(ljump1);
                                mv.visitInsn(ACONST_NULL);
                                BytecodeUtil.doReturn(mv, returnType);
                            }
                        }
                        {
                            // if (obj instanceof Integer)
                            // return ((Integer) obj).intValue();
                            {
                                mv.visitLabel(ljump0);
                                varTb.loadLocal(mv, 2);
                                BytecodeUtil.doInstanceof(mv, returnType);
                                mv.visitJumpInsn(IFEQ, ljump2);
                                varTb.loadLocal(mv, 2);
                                BytecodeUtil.doCast(mv, returnType);
                                BytecodeUtil.doReturn(mv, returnType);
                            }
                            {
                                // else throw new RuntimeException(...);
                                mv.visitLabel(ljump2);
                                mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
                                mv.visitInsn(DUP);
                                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                                mv.visitInsn(DUP);
                                mv.visitLdcInsn("Cache object conflict,key:");
                                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                                varTb.loadLocal(mv, 0);
                                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V");
                                mv.visitInsn(ATHROW);
                            }
                        }
                        mv.visitMaxs(0, 0);
                    }
                    mv.visitEnd();
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
