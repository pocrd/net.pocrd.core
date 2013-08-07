package net.pocrd.util;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodVisitorHelper extends MethodVisitor implements Opcodes {
    private int                     countOfArgs     = 0;
    private int                     nextFreeSlotPos = 0;
    private ArrayList<LocalBuilder> localVarInfo    = new ArrayList<LocalBuilder>();

    public MethodVisitorHelper(int api, MethodVisitor mv) {
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
     * @param indexOfArg
     * if method is not static,args start from "this",
     * else args start from the first parameter of method
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
     * @param indexOfArg
     * if method is not static,args start from "this",
     * else args start from the first parameter of method
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
}
