package net.pocrd.util;

import java.io.File;
import java.io.FileOutputStream;

import net.pocrd.core.PocClassLoader;
import net.pocrd.define.HttpApiExecuter;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.ApiParameterInfo;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 为无状态逻辑类的指定函数产生一个代理，代理接口接受字符串数组，转换后调用原函数
 * 
 * @author rendong
 */
public class HttpApiProvider implements Opcodes {
    private static final String REGEX_PREFIX = "regex_";

    public static HttpApiExecuter getApiExecuter(String name, ApiMethodInfo method) {
        try {
            Class<?> clazz = method.proxyMethodInfo.getDeclaringClass();
            ApiParameterInfo[] parameterInfos = method.parameterInfos;

            String className = "net/pocrd/autogen/ApiExecuter_" + name.replace('.', '_');
            className = className.replace('$', '_');
            String classDesc = "L" + className + ";";
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            FieldVisitor fv;
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
                    new String[] { HttpApiExecuter.class.getName().replace('.', '/') });
            {
                fv = cw.visitField(ACC_PRIVATE, "instance", "Ljava/lang/Object;", null, null);
                fv.visitEnd();
            }
            for (int i = 0; i < parameterInfos.length; i++) {
                ApiParameterInfo parameterInfo = parameterInfos[i];
                if (parameterInfo.verifyRegex != null) {
                    fv = cw.visitField(ACC_PUBLIC, REGEX_PREFIX + parameterInfo.name, "Ljava/util/regex/Pattern;", null, null);
                    fv.visitEnd();
                }
            }
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
                for (int i = 0; i < parameterInfos.length; i++) {
                    ApiParameterInfo parameterInfo = parameterInfos[i];
                    if (parameterInfo.verifyRegex != null) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitLdcInsn(parameterInfo.verifyRegex);
                        mv.visitMethodInsn(INVOKESTATIC, "java/util/regex/Pattern", "compile", "(Ljava/lang/String;)Ljava/util/regex/Pattern;");
                        mv.visitFieldInsn(PUTFIELD, className, REGEX_PREFIX + parameterInfo.name, "Ljava/util/regex/Pattern;");
                    }
                }
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", classDesc, null, l0, l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "setInstance", "(Ljava/lang/Object;)V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, className, "instance", "Ljava/lang/Object;");
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitInsn(RETURN);
                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitLocalVariable("this", classDesc, null, l0, l2, 0);
                mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, l0, l2, 1);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                PoCMethodVisitor pmv = new PoCMethodVisitor(cw, ACC_PUBLIC, "execute", "([Ljava/lang/String;)Ljava/lang/Object;", null, null);
                pmv.visitCode();
                if (parameterInfos.length > 0) {
                    for (int i = 0; i < parameterInfos.length; i++) {
                        ApiParameterInfo parameterInfo = parameterInfos[i];
                        Label l1 = new Label();
                        Label l2 = new Label();
                        if (parameterInfo.isRequired || parameterInfo.verifyRegex != null) {
                            if (parameterInfo.isRequired) {
                                pmv.loadArg(1);
                                pmv.loadConst(i);
                                pmv.visitInsn(AALOAD);
                                if (parameterInfo.verifyRegex != null) {
                                    pmv.visitJumpInsn(IFNULL, l1);
                                } else {
                                    pmv.visitJumpInsn(IFNONNULL, l2);
                                }
                            }
                            if (parameterInfo.verifyRegex != null) {
                                if (!parameterInfo.isRequired) {
                                    pmv.loadArg(1);
                                    pmv.loadConst(i);
                                    pmv.visitInsn(AALOAD);
                                    pmv.visitJumpInsn(IFNULL, l2);
                                }
                                pmv.loadArg(0);
                                pmv.visitFieldInsn(GETFIELD, className, REGEX_PREFIX + parameterInfo.name, "Ljava/util/regex/Pattern;");
                                pmv.loadArg(1);
                                pmv.loadConst(i);
                                pmv.visitInsn(AALOAD);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, "java/util/regex/Pattern", "matcher",
                                        "(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, "java/util/regex/Matcher", "matches", "()Z");
                                pmv.visitJumpInsn(IFNE, l2);
                            }
                            pmv.visitLabel(l1);
                            pmv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
                            pmv.visitInsn(DUP);
                            pmv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "PARAMETER_ERROR", "Lnet/pocrd/entity/ReturnCode;");
                            pmv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                            pmv.visitInsn(DUP);
                            pmv.loadConst("parameter validation failed : " + parameterInfo.name + "=");
                            pmv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                            pmv.loadArg(1);
                            pmv.loadConst(i);
                            pmv.visitInsn(AALOAD);
                            pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                            pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                            pmv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>",
                                    "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/String;)V");
                            pmv.visitInsn(ATHROW);
                            pmv.visitLabel(l2);
                        }
                    }
                    pmv.declareLocal("e", Exception.class);
                    for (int i = 0; i < parameterInfos.length; i++) {
                        Label l1 = new Label();
                        Label l2 = new Label();
                        Label l3 = new Label();
                        ApiParameterInfo parameterInfo = parameterInfos[i];
                        Class<?> parameterType = parameterInfo.type;
                        pmv.declareLocal("" + i, parameterInfo.type);
                        if (parameterType != String.class) {
                            pmv.visitTryCatchBlock(l1, l2, l3, "java/lang/Exception");
                            pmv.visitLabel(l1);
                        }
                        String defaultValueString = null;
                        if (!parameterInfo.isRequired) {
                            defaultValueString = parameterInfo.defaultValue;
                        }
                        pmv.loadArg(1);
                        pmv.loadConst(i);
                        pmv.visitInsn(AALOAD);
                        Label loopLabel2 = new Label();
                        // 参数类型不为String时需要捕获类型转换异常
                        if (defaultValueString != null) {
                            Label loopLabel1 = new Label();
                            pmv.visitJumpInsn(IFNONNULL, loopLabel1);
                            pmv.loadConst(defaultValueString, parameterType);
                            pmv.visitJumpInsn(GOTO, loopLabel2);
                            pmv.visitLabel(loopLabel1);
                            pmv.visitVarInsn(ALOAD, 1);
                            pmv.loadConst(i);
                            pmv.visitInsn(AALOAD);
                        }

                        if (parameterType == boolean.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z");
                        } else if (parameterType == byte.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "parseByte", "(Ljava/lang/String;)B");
                        } else if (parameterType == char.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                            pmv.visitInsn(I2C);
                        } else if (parameterType == short.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "parseShort", "(Ljava/lang/String;)S");
                        } else if (parameterType == int.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                        } else if (parameterType == long.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J");
                        } else if (parameterType == float.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F");
                        } else if (parameterType == double.class) {
                            pmv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "parseDouble", "(Ljava/lang/String;)D");
                        } else if (parameterType == String.class) {
                            // Do nothing
                        } else {
                            throw new RuntimeException("不支持的参数类型" + parameterType.getName());
                        }
                        if (defaultValueString != null) {
                            pmv.visitLabel(loopLabel2);
                        }
                        pmv.setLocal("" + i);
                        // 参数类型不为String时需要捕获类型转换异常
                        if (parameterType != String.class) {
                            pmv.visitLabel(l2);
                            Label label_end = new Label();
                            pmv.visitJumpInsn(GOTO, label_end);
                            pmv.visitLabel(l3);
                            pmv.setLocal("e");
                            pmv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
                            pmv.visitInsn(DUP);
                            pmv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "PARAMETER_ERROR", "Lnet/pocrd/entity/ReturnCode;");
                            pmv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                            pmv.visitInsn(DUP);
                            pmv.loadConst("parameter validation failed : " + parameterInfo.name + "=");
                            pmv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
                            pmv.loadArg(1);
                            pmv.loadConst(i);
                            pmv.visitInsn(AALOAD);
                            pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                            pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                            pmv.loadLocal("e");
                            pmv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>",
                                    "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/String;Ljava/lang/Exception;)V");
                            pmv.visitInsn(ATHROW);
                            pmv.visitLabel(label_end);
                        }
                    }
                }
                pmv.visitVarInsn(ALOAD, 0);
                pmv.visitFieldInsn(GETFIELD, className, "instance", "Ljava/lang/Object;");
                pmv.visitTypeInsn(CHECKCAST, clazz.getName().replace('.', '/'));
                for (int i = 0; i < parameterInfos.length; i++) {
                    pmv.loadLocal("" + i);
                }
                pmv.visitMethodInsn(INVOKEVIRTUAL, clazz.getName().replace('.', '/'), method.proxyMethodInfo.getName(),
                        Type.getMethodDescriptor(method.proxyMethodInfo));
                pmv.visitInsn(ARETURN);
                pmv.visitMaxs(0, 0);
                pmv.visitEnd();
            }
            cw.visitEnd();
            if (CommonConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(CommonConfig.Instance.autogenPath + File.separator + "ApiExecuter" + File.separator);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(CommonConfig.Instance.autogenPath + File.separator + "ApiExecuter" + File.separator + name + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
            HttpApiExecuter e = (HttpApiExecuter)new PocClassLoader(Thread.currentThread().getContextClassLoader()).defineClass(
                    className.replace('/', '.'), cw.toByteArray()).newInstance();
            e.setInstance(clazz.newInstance());
            return e;
        } catch (Exception e) {
            throw new RuntimeException("generate failed. " + name, e);
        }
    }
}
