package net.pocrd.util;

import java.lang.reflect.Modifier;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BytecodeUtil implements Opcodes {
    public static void loadConst(MethodVisitor mv, long l) {
        if (l == 0) {
            mv.visitInsn(LCONST_0);
        } else if (l == 1) {
            mv.visitInsn(LCONST_1);
        } else {
            mv.visitLdcInsn(new Long(l));
        }
    }

    public static void loadConst(MethodVisitor mv, float f) {
        if (f == 0) {
            mv.visitInsn(FCONST_0);
        } else if (f == 1) {
            mv.visitInsn(FCONST_1);
        } else if (f == 2) {
            mv.visitInsn(FCONST_2);
        } else {
            mv.visitLdcInsn(new Float(f));
        }
    }

    public static void loadConst(MethodVisitor mv, double d) {
        if (d == 0) {
            mv.visitInsn(DCONST_0);
        } else if (d == 1) {
            mv.visitInsn(DCONST_1);
        } else {
            mv.visitLdcInsn(new Double(d));
        }
    }

    public static void loadConst(MethodVisitor mv, int i) {
        switch (i) {
            case -1:
                mv.visitInsn(ICONST_M1);
                break;
            case 0:
                mv.visitInsn(ICONST_0);
                break;
            case 1:
                mv.visitInsn(ICONST_1);
                break;
            case 2:
                mv.visitInsn(ICONST_2);
                break;
            case 3:
                mv.visitInsn(ICONST_3);
                break;
            case 4:
                mv.visitInsn(ICONST_4);
                break;
            case 5:
                mv.visitInsn(ICONST_5);
                break;
            default:
                if (i <= Byte.MAX_VALUE && i >= Byte.MIN_VALUE) {
                    mv.visitIntInsn(BIPUSH, i);
                } else if (i <= Short.MAX_VALUE && i >= Short.MIN_VALUE) {
                    mv.visitIntInsn(SIPUSH, i);
                } else {
                    mv.visitLdcInsn(new Integer(i));
                }
                break;
        }
    }

    public static void loadConst(MethodVisitor mv, String s) {
        if (s == null) {
            mv.visitInsn(ACONST_NULL);
        } else {
            mv.visitLdcInsn(s);
        }
    }

    /**
     * 常量 加载
     */
    public static void loadConst(MethodVisitor mv, String s, Class<?> clazz) {
        if (s == null) {
            mv.visitInsn(ACONST_NULL);
        } else {
            if (clazz.isPrimitive()) {
                if (clazz == boolean.class) {
                    loadConst(mv, Boolean.parseBoolean(s) ? 1 : 0);
                } else if (clazz == byte.class) {
                    loadConst(mv, Integer.parseInt(s));
                } else if (clazz == char.class) {
                    loadConst(mv, Integer.parseInt(s));
                } else if (clazz == short.class) {
                    loadConst(mv, Integer.parseInt(s));
                } else if (clazz == int.class) {
                    loadConst(mv, Integer.parseInt(s));
                } else if (clazz == long.class) {
                    loadConst(mv, Long.parseLong(s));
                } else if (clazz == float.class) {
                    loadConst(mv, Float.parseFloat(s));
                } else if (clazz == double.class) {
                    loadConst(mv, Double.parseDouble(s));
                } else {
                    throw new RuntimeException("不支持的参数类型" + clazz.getName());
                }
            } else {
                if (clazz == Boolean.class) {
                    loadConst(mv, Boolean.parseBoolean(s) ? 1 : 0);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                } else if (clazz == Byte.class) {
                    loadConst(mv, Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                } else if (clazz == Character.class) {
                    loadConst(mv, Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                } else if (clazz == Short.class) {
                    loadConst(mv, Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                } else if (clazz == Integer.class) {
                    loadConst(mv, Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                } else if (clazz == Long.class) {
                    loadConst(mv, Long.parseLong(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                } else if (clazz == Float.class) {
                    loadConst(mv, Float.parseFloat(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                } else if (clazz == Double.class) {
                    loadConst(mv, Double.parseDouble(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                } else if (clazz == String.class) {
                    mv.visitLdcInsn(s);
                } else {
                    throw new RuntimeException("不支持的参数类型" + clazz.getName());
                }
            }
        }
    }

    public static void createInnerClassVisitor(ClassWriter cw, Class<?> clazz) {
        Class<?> dc = clazz.getDeclaringClass();
        if (dc != null) {
            createInnerClassVisitor(cw, dc);
        } else {
            return;
        }
        int flag = 0;
        int mod = clazz.getModifiers();
        if (Modifier.isPublic(mod)) {
            flag |= ACC_PUBLIC;
        } else if (Modifier.isProtected(mod)) {
            flag |= ACC_PROTECTED;
        } else if (Modifier.isPrivate(mod)) {
            flag |= ACC_PRIVATE;
        }
        if (Modifier.isFinal(mod)) {
            flag |= ACC_FINAL;
        }
        if (Modifier.isStatic(mod)) {
            flag |= ACC_STATIC;
        }
        if (Modifier.isAbstract(mod)) {
            flag |= ACC_ABSTRACT;
        }
        if (Modifier.isInterface(mod)) {
            flag |= ACC_INTERFACE;
        }
        if (Modifier.isStrict(mod)) {
            flag |= ACC_STRICT;
        }
        cw.visitInnerClass(clazz.getName().replace('.', '/'), dc.getName().replace('.', '/'), clazz.getSimpleName(), flag);
    }

}
