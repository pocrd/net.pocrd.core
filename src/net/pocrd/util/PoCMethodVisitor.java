package net.pocrd.util;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class PoCMethodVisitor extends MethodVisitor implements Opcodes {
    private int                            nextFreeSlotPos = 0;
    private LocalVariable[]                args            = null;
    private HashMap<String, LocalVariable> lvs             = new HashMap<String, LocalVariable>();

    /**
     * 根据签名创建method visitor
     * 
     * @param cw
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     */
    public PoCMethodVisitor(final ClassWriter cw, final int access, final String name, final String desc, final String signature,
            final String[] exceptions) {
        super(ASM4);
        mv = cw.visitMethod(access, name, desc, signature, exceptions);
        declareArgs(access, desc);
    }

    /**
     * 根据method模板复制方法信息
     * 
     * @param cw
     * @param method
     */
    public PoCMethodVisitor(final ClassWriter cw, final Method method) {
        super(ASM4);
        int mod = method.getModifiers();
        String desc = Type.getMethodDescriptor(method);
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        String[] exceptions = null;
        if (exceptionTypes != null && exceptionTypes.length != 0) {
            exceptions = new String[exceptionTypes.length];
            for (int i = 0; i < exceptionTypes.length; i++)
                exceptions[i] = exceptionTypes[i].getName().replace('.', '/');
        }
        mv = cw.visitMethod(mod, method.getName(), desc, null, exceptions);
        declareArgs(mod, desc);
    }

    private void declareArgs(final int access, final String desc) {
        Type[] argTypes = Type.getArgumentTypes(desc);
        int count = argTypes != null ? argTypes.length : 0;
        int hasThis = 0;
        if ((access & ACC_STATIC) != access) {// this
            hasThis = 1;
            count++;
            args = new LocalVariable[count];
            args[0] = new LocalVariable(nextFreeSlotPos, ALOAD, ASTORE);
            nextFreeSlotPos++;
        } else {
            args = new LocalVariable[count];
        }

        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> clazz = argTypes[i].getClass();
                if (clazz == int.class || clazz == boolean.class || clazz == short.class || clazz == byte.class || clazz == char.class) {
                    args[i + hasThis] = new LocalVariable(nextFreeSlotPos, ILOAD, ISTORE);
                } else if (clazz == float.class) {
                    args[i + hasThis] = new LocalVariable(nextFreeSlotPos, FLOAD, FSTORE);
                } else if (clazz == double.class) {
                    args[i + hasThis] = new LocalVariable(nextFreeSlotPos, DLOAD, DSTORE);
                    nextFreeSlotPos++;
                } else if (clazz == long.class) {
                    args[i + hasThis] = new LocalVariable(nextFreeSlotPos, LLOAD, LSTORE);
                    nextFreeSlotPos++;
                } else {
                    args[i + hasThis] = new LocalVariable(nextFreeSlotPos, ALOAD, ASTORE);
                }
                nextFreeSlotPos++;
            }
        }
    }

    public void setArg(int index) {
        LocalVariable vinfo = args[index];
        mv.visitVarInsn(vinfo.getStoreOpcode(), vinfo.getSlotPos());
    }

    public void loadArg(int index) {
        LocalVariable vinfo = args[index];
        mv.visitVarInsn(vinfo.getLoadOpcode(), vinfo.getSlotPos());
    }

    /**
     * 局部变量声明,可无序
     * 
     * @param mv
     * @param clazz
     */
    public void declareLocal(String localName, Class<?> clazz) {
        LocalVariable local = null;
        if (clazz.isPrimitive()) {
            if (clazz == int.class || clazz == boolean.class || clazz == short.class || clazz == byte.class || clazz == char.class) {
                local = new LocalVariable(nextFreeSlotPos, ILOAD, ISTORE);
            } else if (clazz == float.class) {
                local = new LocalVariable(nextFreeSlotPos, FLOAD, FSTORE);
            } else if (clazz == double.class) {
                // 32位机，double/long占两个slot
                local = new LocalVariable(nextFreeSlotPos, DLOAD, DSTORE);
                nextFreeSlotPos++;
            } else if (clazz == long.class) {
                local = new LocalVariable(nextFreeSlotPos, LLOAD, LSTORE);
                nextFreeSlotPos++;
            } else {
                throw new RuntimeException("不支持的类型" + clazz.getName());
            }
        } else {
            local = new LocalVariable(nextFreeSlotPos, ALOAD, ASTORE);
        }
        lvs.put(localName, local);
        nextFreeSlotPos++;
    }

    /**
     * 存放局部变量
     * 
     * @param mv
     * @param indexOfLocal
     */
    public void setLocal(String localName) {
        LocalVariable lv = lvs.get(localName);
        if (lv != null) {
            mv.visitVarInsn(lv.getStoreOpcode(), lv.getSlotPos());
        } else {
            throw new RuntimeException("local variable : " + localName + " not defined.");
        }
    }

    /**
     * 加载局部变量
     * 
     * @param mv
     * @param lb
     */
    public void loadLocal(String localName) {
        LocalVariable lv = lvs.get(localName);
        if (lv != null) {
            mv.visitVarInsn(lv.getLoadOpcode(), lv.getSlotPos());
        } else {
            throw new RuntimeException("local variable : " + localName + " not defined.");
        }
    }

    /**
     * 类型转换
     * 
     * @param mv
     * @param clazz
     */
    public void doCast(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            } else if (clazz == char.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            } else if (clazz == short.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            } else if (clazz == byte.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            } else if (clazz == float.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            } else if (clazz == boolean.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            } else if (clazz == double.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
            } else if (clazz == long.class) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            } else throw new RuntimeException("不支持的类型" + clazz.getName());
        } else {
            mv.visitTypeInsn(CHECKCAST, clazz.getName().replace('.', '/'));
        }
    }

    /**
     * 值类型装箱
     * 
     * @param mv
     * @param clazz
     */
    public void doInbox(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            else if (clazz == char.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            else if (clazz == short.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            else if (clazz == byte.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            else if (clazz == float.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            else if (clazz == boolean.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            else if (clazz == double.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            else if (clazz == long.class)
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            else throw new RuntimeException("不支持的类型" + clazz.getName());
        }
    }

    /**
     * 方法返回
     * 
     * @param mv
     * @param clazz
     */
    public void doReturn(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class || clazz == char.class || clazz == short.class || clazz == byte.class || clazz == float.class
                    || clazz == boolean.class)
                mv.visitInsn(IRETURN);
            else if (clazz == double.class)
                mv.visitInsn(DRETURN);
            else if (clazz == long.class)
                mv.visitInsn(LRETURN);
            else if (clazz == void.class)
                mv.visitInsn(RETURN);
            else throw new RuntimeException("不支持的类型" + clazz.getName());
        } else {
            mv.visitInsn(ARETURN);
        }
    }

    /**
     * @param mv
     * @param clazz
     */
    public void doInstanceof(Class<?> clazz) {
        String type = null;
        if (clazz.isPrimitive()) {
            if (clazz == int.class)
                type = "java/lang/Integer";
            else if (clazz == char.class)
                type = "java/lang/Char";
            else if (clazz == short.class)
                type = "java/lang/Short";
            else if (clazz == byte.class)
                type = "java/lang/Byte";
            else if (clazz == float.class)
                type = "java/lang/Float";
            else if (clazz == boolean.class)
                type = "java/lang/Boolean";
            else if (clazz == double.class)
                type = "java/lang/Double";
            else if (clazz == long.class)
                type = "java/lang/Long";
            else throw new RuntimeException("不支持的类型" + clazz.getName());
        } else {
            type = clazz.getName().replace('.', '/');
        }
        mv.visitTypeInsn(INSTANCEOF, type);
    }

    public void loadConst(long l) {
        if (l == 0) {
            mv.visitInsn(LCONST_0);
        } else if (l == 1) {
            mv.visitInsn(LCONST_1);
        } else {
            mv.visitLdcInsn(new Long(l));
        }
    }

    public void loadConst(float f) {
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

    public void loadConst(double d) {
        if (d == 0) {
            mv.visitInsn(DCONST_0);
        } else if (d == 1) {
            mv.visitInsn(DCONST_1);
        } else {
            mv.visitLdcInsn(new Double(d));
        }
    }

    public void loadConst(int i) {
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

    public void loadConst(String s) {
        if (s == null) {
            mv.visitInsn(ACONST_NULL);
        } else {
            mv.visitLdcInsn(s);
        }
    }

    /**
     * 常量 加载
     */
    public void loadConst(String s, Class<?> clazz) {
        if (s == null) {
            mv.visitInsn(ACONST_NULL);
        } else {
            if (clazz.isPrimitive()) {
                if (clazz == boolean.class) {
                    loadConst(Boolean.parseBoolean(s) ? 1 : 0);
                } else if (clazz == byte.class) {
                    loadConst(Byte.parseByte(s));
                } else if (clazz == char.class) {
                    loadConst(Integer.parseInt(s));
                } else if (clazz == short.class) {
                    loadConst(Integer.parseInt(s));
                } else if (clazz == int.class) {
                    loadConst(Integer.parseInt(s));
                } else if (clazz == long.class) {
                    loadConst(Long.parseLong(s));
                } else if (clazz == float.class) {
                    loadConst(Float.parseFloat(s));
                } else if (clazz == double.class) {
                    loadConst(Double.parseDouble(s));
                } else {
                    throw new RuntimeException("不支持的参数类型" + clazz.getName());
                }
            } else {
                if (clazz == Boolean.class) {
                    loadConst(Boolean.parseBoolean(s) ? 1 : 0);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                } else if (clazz == Byte.class) {
                    loadConst(Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                } else if (clazz == Character.class) {
                    loadConst(Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                } else if (clazz == Short.class) {
                    loadConst(Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                } else if (clazz == Integer.class) {
                    loadConst(Integer.parseInt(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                } else if (clazz == Long.class) {
                    loadConst(Long.parseLong(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                } else if (clazz == Float.class) {
                    loadConst(Float.parseFloat(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                } else if (clazz == Double.class) {
                    loadConst(Double.parseDouble(s));
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                } else if (clazz == String.class) {
                    mv.visitLdcInsn(s);
                } else {
                    throw new RuntimeException("不支持的参数类型" + clazz.getName());
                }
            }
        }
    }
}
