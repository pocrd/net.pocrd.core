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

    public static HttpApiExecuter getApiExecuter(String name, ApiMethodInfo method) {
        try {
            Class<?> clazz = method.proxyMethodInfo.getDeclaringClass();
            ApiParameterInfo[] parameterInfos = method.parameterInfos;

            String className = "net/pocrd/autogen/ApiExecuter_" + name.replace('.', '_');
            className = className.replace('$', '_');
            String classDesc = "L" + className + ";";
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            FieldVisitor fv;
            MethodVisitor mv;

            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
                    new String[] { HttpApiExecuter.class.getName().replace('.', '/') });
            {
                fv = cw.visitField(ACC_PRIVATE, "instance", "Ljava/lang/Object;", null, null);
                fv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", classDesc, null, l0, l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "setInstance", "(Ljava/lang/Object;)V", null, null);
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
                mv = cw.visitMethod(ACC_PUBLIC, "execute", "([Ljava/lang/String;)Ljava/lang/Object;", null, null);
                mv.visitCode();
                //Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                Label l3 = new Label();
                Label l4 = new Label();
                mv.visitTryCatchBlock(l2, l3, l4, "java/lang/Exception");
                //mv.visitLabel(l0);
                int notNullCount = 0;
                for (int i = 0; i < parameterInfos.length; i++) {
                    ApiParameterInfo parameterInfo = parameterInfos[i];
                    if (parameterInfo.isRequired || (parameterInfo.defaultValue == null && parameterInfo.getRawType().isPrimitive())) {
                        notNullCount++;
                    }
                }
                for (int i = 0; i < parameterInfos.length; i++) {
                    ApiParameterInfo parameterInfo = parameterInfos[i];
                    if (parameterInfo.isRequired || (parameterInfo.defaultValue == null && parameterInfo.getRawType().isPrimitive())) {
                        mv.visitVarInsn(ALOAD, 1);
                        BytecodeUtil.loadConst(mv, i);
                        mv.visitInsn(AALOAD);
                        notNullCount--;
                        if (notNullCount == 0) {
                            mv.visitJumpInsn(IFNONNULL, l2);
                        } else {
                            mv.visitJumpInsn(IFNULL, l1);
                        }
                    }
                }
                mv.visitLabel(l1);
                mv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
                mv.visitInsn(DUP);
                mv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "PARAMETER_ERROR", "Lnet/pocrd/entity/ReturnCode;");
                mv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>", "(Lnet/pocrd/entity/ReturnCode;)V");
                mv.visitInsn(ATHROW);
                mv.visitLabel(l2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "instance", "Ljava/lang/Object;");
                mv.visitTypeInsn(CHECKCAST, clazz.getName().replace('.', '/'));
                for (int i = 0; i < parameterInfos.length; i++) {
                    ApiParameterInfo parameterInfo = parameterInfos[i];
                    Class<?> parameterType = parameterInfo.getRawType();
                    String defaultValueString = null;
                    if (!parameterInfo.isRequired) {
                        defaultValueString = parameterInfo.defaultValue;
                    }
                    mv.visitVarInsn(ALOAD, 1);
                    BytecodeUtil.loadConst(mv, i);
                    mv.visitInsn(AALOAD);
                    Label loopLabel2 = new Label();
                    if (defaultValueString != null) {
                        Label loopLabel1 = new Label();
                        mv.visitJumpInsn(IFNONNULL, loopLabel1);
                        BytecodeUtil.loadConst(mv, defaultValueString, parameterType);
                        mv.visitJumpInsn(GOTO, loopLabel2);
                        mv.visitLabel(loopLabel1);
                        mv.visitVarInsn(ALOAD, 1);
                        BytecodeUtil.loadConst(mv, i);
                        mv.visitInsn(AALOAD);
                    }
                    if (parameterType.isPrimitive()) {
                        String pName = parameterType.toString();
                        if ("boolean".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z");
                        } else if ("byte".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "parseByte", "(Ljava/lang/String;)B");
                        } else if ("char".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                            mv.visitInsn(I2C);
                        } else if ("short".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "parseShort", "(Ljava/lang/String;)S");
                        } else if ("int".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                        } else if ("long".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J");
                        } else if ("float".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F");
                        } else if ("double".equals(pName)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "parseDouble", "(Ljava/lang/String;)D");
                        } else {
                            throw new RuntimeException("不支持的参数类型" + pName);
                        }
                    } else {
                        if (parameterType == Boolean.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Ljava/lang/String;)Ljava/lang/Boolean;");
                        } else if (parameterType == Byte.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(Ljava/lang/String;)Ljava/lang/Byte;");
                        } else if (parameterType == Character.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                            mv.visitInsn(I2C);
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                        } else if (parameterType == Short.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(Ljava/lang/String;)Ljava/lang/Short;");
                        } else if (parameterType == Integer.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;");
                        } else if (parameterType == Long.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(Ljava/lang/String;)Ljava/lang/Long;");
                        } else if (parameterType == Float.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(Ljava/lang/String;)Ljava/lang/Float;");
                        } else if (parameterType == Double.class) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(Ljava/lang/String;)Ljava/lang/Double;");
                        } else if (parameterType == String.class) {
                            // Do nothing
                        } else {
                            throw new RuntimeException("不支持的参数类型" + parameterType.getName());
                        }
                    }
                    if (defaultValueString != null) {
                        mv.visitLabel(loopLabel2);
                    }
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, clazz.getName().replace('.', '/'), method.proxyMethodInfo.getName(),
                        Type.getMethodDescriptor(method.proxyMethodInfo));
                mv.visitLabel(l3);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l4);
                mv.visitVarInsn(ASTORE, 2);
                // Label l5 = new Label();
                // mv.visitLabel(l5);
                mv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
                mv.visitInsn(DUP);
                mv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "PARAMETER_ERROR", "Lnet/pocrd/entity/ReturnCode;");
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>",
                        "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/Exception;)V");
                mv.visitInsn(ATHROW);
                // Label l6 = new Label();
                // mv.visitLabel(l6);
                // mv.visitLocalVariable("this", classDesc, null, l0, l6, 0);
                // mv.visitLocalVariable("parameters", "[Ljava/lang/String;", null, l0, l6, 1);
                // mv.visitLocalVariable("e", "Ljava/lang/Exception;", null, l5, l6, 2);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            cw.visitEnd();
            if (CommonConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(CommonConfig.Instance.autogenPath + "\\ApiExecuter\\");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(CommonConfig.Instance.autogenPath + "\\ApiExecuter\\" + name + ".class");
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
