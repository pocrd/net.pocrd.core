package net.pocrd.util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MethodVisitorWrapper extends MethodVisitor implements Opcodes {
    private int                      countOfArgs     = 0;
    private int                      nextFreeSlotPos = 0;
    private ArrayList<LocalVariable> localVarInfo    = new ArrayList<LocalVariable>();

    /**
     * 根据指定信息创建方法签名
     *
     * @param cw
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     */
    public MethodVisitorWrapper(final ClassWriter cw, final int access, final String name, final String desc, final String signature,
                                final String[] exceptions) {
        super(ASM4);
        mv = cw.visitMethod(access, name, desc, signature, exceptions);
        declareArgs(access, desc);
    }

    /**
     * 根据method模板复制方法签名
     *
     * @param cw
     * @param method
     */
    public MethodVisitorWrapper(final ClassWriter cw, final Method method) {
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

    /**
     * 根据函数签名计算栈长度
     *
     * @param access
     * @param desc
     */
    private void declareArgs(final int access, final String desc) {
        nextFreeSlotPos = 0;
        Type[] argTypes = Type.getArgumentTypes(desc);
        countOfArgs = argTypes != null ? argTypes.length : 0;
        if ((access & ACC_STATIC) != access) {// this
            countOfArgs++;
            localVarInfo.add(new LocalVariable(nextFreeSlotPos, ALOAD, ASTORE));
            nextFreeSlotPos++;
        }
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                String argClassName = argTypes[i].getClassName();
                if ("int".equals(argClassName) || "boolean".equals(argClassName) || "short".equals(argClassName) || "byte".equals(
                        argClassName) || "char".equals(argClassName)) {
                    localVarInfo.add(new LocalVariable(nextFreeSlotPos, ILOAD, ISTORE));
                } else if ("float".equals(argClassName)) {
                    localVarInfo.add(new LocalVariable(nextFreeSlotPos, FLOAD, FSTORE));
                } else if ("double".equals(argClassName)) {
                    // 32位机，double/long占两个slot
                    localVarInfo.add(new LocalVariable(nextFreeSlotPos, DLOAD, DSTORE));
                    nextFreeSlotPos++;
                } else if ("long".equals(argClassName)) {
                    localVarInfo.add(new LocalVariable(nextFreeSlotPos, LLOAD, LSTORE));
                    nextFreeSlotPos++;
                } else {
                    localVarInfo.add(new LocalVariable(nextFreeSlotPos, ALOAD, ASTORE));
                }
                nextFreeSlotPos++;
            }
        }
    }

    /**
     * 修改函数入参的值
     *
     * @param mv
     * @param indexOfArg if method is not static,args start from "this", else args start from the first parameter of method
     */
    public void setArg(int indexOfArg) {
        if (countOfArgs > indexOfArg) {
            LocalVariable vinfo = localVarInfo.get(indexOfArg);
            if (vinfo != null) {
                mv.visitVarInsn(vinfo.getStoreOpcode(), vinfo.getSlotPos());
            } else { throw new RuntimeException("参数未声明,index:" + indexOfArg); }
        } else {
            throw new RuntimeException("参数索引非法,index:" + indexOfArg);
        }
    }

    /**
     * 加载函数入参
     *
     * @param mv
     * @param indexOfArg if method is not static,args start from "this", else args start from the first parameter of method
     */
    public void loadArg(int indexOfArg) {
        if (countOfArgs > indexOfArg) {
            LocalVariable vinfo = localVarInfo.get(indexOfArg);
            if (vinfo != null) {
                mv.visitVarInsn(vinfo.getLoadOpcode(), vinfo.getSlotPos());
            } else { throw new RuntimeException("参数未声明,index:" + indexOfArg); }
        } else {
            throw new RuntimeException("参数索引非法,index:" + indexOfArg);
        }
    }

    /**
     * 局部变量声明
     *
     * @param clazz
     */
    public LocalVariable declareLocal(Class<?> clazz) {
        LocalVariable localBuilder = null;
        if (clazz != null) {
            if (clazz.isPrimitive()) {
                String pName = clazz.getName();
                if ("int".equals(pName) || "boolean".equals(pName) || "short".equals(pName) || "byte".equals(pName) || "char".equals(pName)) {
                    localBuilder = new LocalVariable(nextFreeSlotPos, ILOAD, ISTORE);
                } else if ("float".equals(pName)) {
                    localBuilder = new LocalVariable(nextFreeSlotPos, FLOAD, FSTORE);
                } else if ("double".equals(pName)) {
                    // 32位机，double/long占两个slot
                    localBuilder = new LocalVariable(nextFreeSlotPos, DLOAD, DSTORE);
                    nextFreeSlotPos++;
                } else if ("long".equals(pName)) {
                    localBuilder = new LocalVariable(nextFreeSlotPos, LLOAD, LSTORE);
                    nextFreeSlotPos++;
                } else { throw new RuntimeException("不支持的类型" + clazz.getName()); }
            } else {
                localBuilder = new LocalVariable(nextFreeSlotPos, ALOAD, ASTORE);
            }
            localVarInfo.add(localBuilder);
            nextFreeSlotPos++;
        }
        return localBuilder;
    }

    /**
     * 存储局部变量
     *
     * @param mv
     * @param indexOfLocal
     */
    public void setLocal(LocalVariable lb) {
        if (lb != null) {
            mv.visitVarInsn(lb.getStoreOpcode(), lb.getSlotPos());
        }
    }

    /**
     * 加载局部变量
     *
     * @param mv
     * @param lb
     */
    public void loadLocal(LocalVariable lb) {
        if (lb != null) {
            mv.visitVarInsn(lb.getLoadOpcode(), lb.getSlotPos());
        }
    }

    /**
     * 强制类型转换
     *
     * @param clazz
     */
    public void doCast(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            String className = clazz.getName();
            if ("int".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            } else if ("char".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
            } else if ("short".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            } else if ("byte".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            } else if ("float".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            } else if ("boolean".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
            } else if ("double".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
            } else if ("long".equals(className)) {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            } else { throw new RuntimeException("不支持的类型" + clazz.getName()); }
        } else {
            mv.visitTypeInsn(CHECKCAST, clazz.getName().replace('.', '/'));
        }
    }

    /**
     * 值类型装箱
     *
     * @param clazz
     */
    public void doInbox(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            String className = clazz.getName();
            if ("int".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else if ("char".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            } else if ("short".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            } else if ("byte".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            } else if ("float".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            } else if ("boolean".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            } else if ("double".equals(className)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            } else if ("long".equals(className)) { mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;"); } else {
                throw new RuntimeException("不支持的类型" + clazz.getName());
            }
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
            String className = clazz.getName();
            if ("int".equals(className) || "char".equals(className) || "short".equals(className) || "byte".equals(className) || "float".equals(
                    className) || "boolean".equals(className)) {
                mv.visitInsn(IRETURN);
            } else if ("double".equals(className)) { mv.visitInsn(DRETURN); } else if ("long".equals(className)) {
                mv.visitInsn(LRETURN);
            } else if ("void".equals(className)) { mv.visitInsn(RETURN); } else throw new RuntimeException("不支持的类型" + clazz.getName());
        } else {
            mv.visitInsn(ARETURN);
        }
    }

    /**
     * 类型判断
     *
     * @param clazz
     */
    public void doInstanceof(Class<?> clazz) {
        String type = null;
        if (clazz.isPrimitive()) {
            String className = clazz.getName();
            if ("int".equals(className)) { type = "java/lang/Integer"; } else if ("char".equals(className)) {
                type = "java/lang/Char";
            } else if ("short".equals(className)) {
                type = "java/lang/Short";
            } else if ("byte".equals(className)) {
                type = "java/lang/Byte";
            } else if ("float".equals(className)) {
                type = "java/lang/Float";
            } else if ("boolean".equals(className)) {
                type = "java/lang/Boolean";
            } else if ("double".equals(className)) {
                type = "java/lang/Double";
            } else if ("long".equals(className)) { type = "java/lang/Long"; } else throw new RuntimeException("不支持的类型" + clazz.getName());
        } else {
            type = clazz.getName().replace('.', '/');
        }
        mv.visitTypeInsn(INSTANCEOF, type);
    }

    /**
     * 长整形常量加载
     *
     * @param l
     */
    public void loadConst(long l) {
        if (l == 0) {
            mv.visitInsn(LCONST_0);
        } else if (l == 1) {
            mv.visitInsn(LCONST_1);
        } else {
            mv.visitLdcInsn(Long.valueOf(l));
        }
    }

    /**
     * 单精度浮点型常量加载
     *
     * @param f
     */
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

    /**
     * 双精度浮点型常量加载
     *
     * @param d
     */
    public void loadConst(double d) {
        if (d == 0) {
            mv.visitInsn(DCONST_0);
        } else if (d == 1) {
            mv.visitInsn(DCONST_1);
        } else {
            mv.visitLdcInsn(new Double(d));
        }
    }

    /**
     * 整数型常量加载
     *
     * @param i
     */
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
                    mv.visitLdcInsn(Integer.valueOf(i));
                }
                break;
        }
    }

    /**
     * 字符串型常量加载
     *
     * @param s
     */
    public void loadConst(String s) {
        if (s == null) {
            mv.visitInsn(ACONST_NULL);
        } else {
            mv.visitLdcInsn(s);
        }
    }

    /**
     * 指定类型常量加载
     */
    public void loadConst(String s, Class<?> clazz) {
        if (s == null) {
            mv.visitInsn(ACONST_NULL);
        } else {
            if (clazz.isPrimitive()) {
                String pName = clazz.toString();
                if ("boolean".equals(pName)) {
                    loadConst(Boolean.parseBoolean(s) ? 1 : 0);
                } else if ("byte".equals(pName)) {
                    loadConst(Integer.parseInt(s));
                } else if ("char".equals(pName)) {
                    loadConst(Integer.parseInt(s));
                } else if ("short".equals(pName)) {
                    loadConst(Integer.parseInt(s));
                } else if ("int".equals(pName)) {
                    loadConst(Integer.parseInt(s));
                } else if ("long".equals(pName)) {
                    loadConst(Long.parseLong(s));
                } else if ("float".equals(pName)) {
                    loadConst(Float.parseFloat(s));
                } else if ("double".equals(pName)) {
                    loadConst(Double.parseDouble(s));
                } else {
                    throw new RuntimeException("不支持的参数类型" + pName);
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
