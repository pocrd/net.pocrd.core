package net.pocrd.util;

import net.pocrd.core.PocClassLoader;
import net.pocrd.define.ApiMixer;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.ApiParameterInfo;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.CompileConfig;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rendong on 2018/5/28.
 */
public class HttpMixerProvider implements Opcodes {

    private static final ConcurrentHashMap<String, ApiMixer> cache = new ConcurrentHashMap<String, ApiMixer>();

    public static ApiMixer getMixerExecutor(String name, ApiMethodInfo method) {
        String key = name;
        ApiMixer mixer = cache.get(key);
        if (mixer == null) {
            synchronized (cache) {
                mixer = cache.get(key);
                if (mixer == null) {
                    mixer = createMixerExecutor(name, method);
                    cache.put(key, mixer);
                }
            }
        }
        return mixer;
    }

    /**
     * 由于jdk 1.8 改用 Metaspace 后重复调用 defineClass 可能导致内存泄漏, 要求所有直接产生字节码的工具类进行本地缓存。
     */
    public synchronized static ApiMixer createMixerExecutor(String name, ApiMethodInfo method) {
        try {
            Class<?> clazz = method.proxyMethodInfo.getDeclaringClass();
            String className = "net/pocrd/autogen/ApiMixer_" + name.replace('.', '_');
            className = className.replace('$', '_');
            ClassWriter cw = new PocClassWriter(ClassWriter.COMPUTE_FRAMES);
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", new String[] { Type.getInternalName(ApiMixer.class) });
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
                mv.visitInsn(RETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            {
                PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_PUBLIC, "execute", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
                pmv.visitCode();

                int i = 0;
                for (ApiParameterInfo p : method.parameterInfos) {
                    pmv.loadArg(1);
                    pmv.loadConst(i);
                    pmv.visitInsn(AALOAD);
                    Label checkParamNull = new Label();
                    pmv.visitJumpInsn(IFNONNULL, checkParamNull);
                    String pname = "p" + i;
                    pmv.declareRefLocal(pname);
                    pmv.visitInsn(ACONST_NULL);
                    pmv.setLocal(pname);
                    Label end = new Label();
                    pmv.visitJumpInsn(GOTO, end);
                    pmv.visitLabel(checkParamNull);
                    pmv.visitTypeInsn(NEW, Type.getInternalName(p.type));
                    pmv.visitInsn(DUP);
                    pmv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(p.type), "<init>", "()V");
                    pmv.setLocal(pname);
                    pmv.visitLdcInsn(Type.getType(p.type));
                    pmv.loadArg(1);
                    pmv.loadConst(i);
                    pmv.visitInsn(AALOAD);
                    pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                    pmv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/EvaluatorProvider", "getEvaluator",
                            "(Ljava/lang/Class;Ljava/lang/Class;)Lnet/pocrd/define/Evaluator;");
                    pmv.loadLocal(pname);
                    pmv.loadArg(1);
                    pmv.loadConst(i);
                    pmv.visitInsn(AALOAD);
                    pmv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Evaluator", "evaluate", "(Ljava/lang/Object;Ljava/lang/Object;)V");
                    pmv.visitLabel(end);
                    i++;
                }

                StringBuilder sb = new StringBuilder("(");
                for (int j = 0; j < i; j++) {
                    pmv.loadLocal("p" + j);
                    sb.append(Type.getDescriptor(method.parameterInfos[j].type));
                }
                sb.append(")");
                sb.append(Type.getDescriptor(method.returnType));

                pmv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(clazz), "mix", sb.toString());
                pmv.visitInsn(ARETURN);
                pmv.visitMaxs(0, 0);
                pmv.visitEnd();
            }
            cw.visitEnd();
            if (CompileConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(CommonConfig.getInstance().getAutogenPath() + File.separator + "ApiMixer" + File.separator);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(
                            CommonConfig.getInstance().getAutogenPath() + File.separator + "ApiMixer" + File.separator + name + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
            ApiMixer e = (ApiMixer)new PocClassLoader(Thread.currentThread().getContextClassLoader())
                    .defineClass(className.replace('/', '.'), cw.toByteArray()).newInstance();
            return e;
        } catch (Throwable t) {
            throw new RuntimeException("generator failed. " + name, t);
        }
    }
}
