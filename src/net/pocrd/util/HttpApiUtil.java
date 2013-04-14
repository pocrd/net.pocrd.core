package net.pocrd.util;

import java.io.FileOutputStream;
import java.lang.reflect.Method;

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
public class HttpApiUtil implements Opcodes {

    public static HttpApiExecuter getApiExecuter(String name, Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            Class<?>[] parameterTypes = method.getParameterTypes();
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
                Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "instance", "Ljava/lang/Object;");
                mv.visitTypeInsn(CHECKCAST, clazz.getName().replace('.', '/'));
                for (int i = 0; i < parameterTypes.length; i++) {
                    mv.visitVarInsn(ALOAD, 1);
                    BytecodeUtil.loadConstInt(mv, i);
                    mv.visitInsn(AALOAD);
                    if (parameterTypes[i] == Boolean.class || "boolean".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                        }
                    } else if (parameterTypes[i] == Byte.class || "byte".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "parseByte", "(Ljava/lang/String;)B");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                        }
                    } else if (parameterTypes[i] == Character.class || "char".equals(parameterTypes[i].toString())) {
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                        }
                    } else if (parameterTypes[i] == Short.class || "short".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "parseShort", "(Ljava/lang/String;)S");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                        }
                    } else if (parameterTypes[i] == Integer.class || "int".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        }
                    } else if (parameterTypes[i] == Long.class || "long".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                        }
                    } else if (parameterTypes[i] == Float.class || "float".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                        }
                    } else if (parameterTypes[i] == Double.class || "double".equals(parameterTypes[i].toString())) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "parseDouble", "(Ljava/lang/String;)D");
                        if (!parameterTypes[i].isPrimitive()) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        }
                    } else if (parameterTypes[i] == String.class) {

                    } else {
                        throw new RuntimeException("不支持的参数类型" + parameterTypes[i].getName());
                    }
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, clazz.getName().replace('.', '/'), method.getName(), Type.getMethodDescriptor(method));
                mv.visitLabel(l1);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Exception" });
                mv.visitVarInsn(ASTORE, 2);
                Label l5 = new Label();
                mv.visitLabel(l5);
                mv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
                mv.visitInsn(DUP);
                mv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "PARAMETER_ERROR", "Lnet/pocrd/entity/ReturnCode;");
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>",
                        "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/Exception;)V");
                mv.visitInsn(ATHROW);
                Label l6 = new Label();
                mv.visitLabel(l6);
                mv.visitLocalVariable("this", classDesc, null, l0, l6, 0);
                mv.visitLocalVariable("parameters", "[Ljava/lang/String;", null, l0, l6, 1);
                mv.visitLocalVariable("e", "Ljava/lang/Exception;", null, l5, l6, 2);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            cw.visitEnd();
            new FileOutputStream("E:\\software\\jd\\t.class").write(cw.toByteArray());
            HttpApiExecuter e = (HttpApiExecuter)new PocClassLoader().defineClass(className.replace('/', '.'), cw.toByteArray()).newInstance();
            e.setInstance(clazz.newInstance());
            return e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
