package net.pocrd.util;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodVisitorWrapper extends MethodVisitor implements Opcodes {
    private int                     countOfArgs     = 0;
    private int                     nextFreeSlotPos = 0;
    private ArrayList<LocalBuilder> localVarInfo    = new ArrayList<LocalBuilder>();

    public MethodVisitorWrapper(int api, MethodVisitor mv) {
        super(api, mv);
    }

    /**
     * 声明所有函数入参
     * 
     * @param isStatic
     * @param paramTypes
     */
    public void declareArgs(boolean isStatic, Class<?>[] paramTypes) {
        nextFreeSlotPos = 0;
        countOfArgs = paramTypes != null ? paramTypes.length : 0;
        if (!isStatic) {// this
            declareLocal(Object.class);
            countOfArgs++;
        }
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; i++) {
                declareLocal(paramTypes[i]);
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
            LocalBuilder vinfo = localVarInfo.get(indexOfArg);
            if (vinfo != null) {
                mv.visitVarInsn(vinfo.getStoreOpcode(), vinfo.getSlotPos());
            } else throw new RuntimeException("参数未声明,index:" + indexOfArg);
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
            LocalBuilder vinfo = localVarInfo.get(indexOfArg);
            if (vinfo != null) {
                mv.visitVarInsn(vinfo.getLoadOpcode(), vinfo.getSlotPos());
            } else throw new RuntimeException("参数未声明,index:" + indexOfArg);
        } else {
            throw new RuntimeException("参数索引非法,index:" + indexOfArg);
        }
    }

    /**
     * 局部变量声明,可无序
     * 
     * @param mv
     * @param clazz
     */
    public LocalBuilder declareLocal(Class<?> clazz) {
        LocalBuilder localBuilder = null;
        if (clazz != null) {
            if (clazz.isPrimitive()) {
                String pName = clazz.getName();
                if ("int".equals(pName) || "boolean".equals(pName) || "short".equals(pName) || "byte".equals(pName) || "char".equals(pName)) {
                    localBuilder = new LocalBuilder(nextFreeSlotPos, ILOAD, ISTORE);
                } else if ("float".equals(pName)) {
                    localBuilder = new LocalBuilder(nextFreeSlotPos, FLOAD, FSTORE);
                } else if ("double".equals(pName)) {
                    // 32位机，double/long占两个slot
                    localBuilder = new LocalBuilder(nextFreeSlotPos, DLOAD, DSTORE);
                    nextFreeSlotPos++;
                } else if ("long".equals(pName)) {
                    localBuilder = new LocalBuilder(nextFreeSlotPos, LLOAD, LSTORE);
                    nextFreeSlotPos++;
                } else throw new RuntimeException("不支持的类型" + clazz.getName());
            } else {
                localBuilder = new LocalBuilder(nextFreeSlotPos, ALOAD, ASTORE);
            }
            localVarInfo.add(localBuilder);
            nextFreeSlotPos++;
        }
        return localBuilder;
    }

    /**
     * 存放局部变量
     * 
     * @param mv
     * @param indexOfLocal
     */
    public void setLocal(LocalBuilder lb) {
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
    public void loadLocal(LocalBuilder lb) {
        if (lb != null) {
            mv.visitVarInsn(lb.getLoadOpcode(), lb.getSlotPos());
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
            String className = clazz.getName();
            if ("int".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            else if ("char".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            else if ("short".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            else if ("byte".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            else if ("float".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            else if ("boolean".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            else if ("double".equals(className))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            else if ("long".equals(className))
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
            String className = clazz.getName();
            if ("int".equals(className) || "char".equals(className) || "short".equals(className) || "byte".equals(className)
                    || "float".equals(className) || "boolean".equals(className))
                mv.visitInsn(IRETURN);
            else if ("double".equals(className))
                mv.visitInsn(DRETURN);
            else if ("long".equals(className))
                mv.visitInsn(LRETURN);
            else if ("void".equals(className))
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
            String className = clazz.getName();
            if ("int".equals(className))
                type = "java/lang/Integer";
            else if ("char".equals(className))
                type = "java/lang/Char";
            else if ("short".equals(className))
                type = "java/lang/Short";
            else if ("byte".equals(className))
                type = "java/lang/Byte";
            else if ("float".equals(className))
                type = "java/lang/Float";
            else if ("boolean".equals(className))
                type = "java/lang/Boolean";
            else if ("double".equals(className))
                type = "java/lang/Double";
            else if ("long".equals(className))
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
