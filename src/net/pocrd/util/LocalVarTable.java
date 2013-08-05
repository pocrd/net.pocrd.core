package net.pocrd.util;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVarTable implements Opcodes {
    private class VarInfo {
        public VarInfo(int slotPos, int loadOpcode) {
            this.slotPos = slotPos;
            this.loadOpcode = loadOpcode;
        }

        /**
         * 加载变量的指令
         */
        public int loadOpcode;
        /**
         * 变量在局部变量表中的存储位置
         */
        public int slotPos;
    }

    private int                countOfArgs     = 0;
    private int                nextFreeSlotPos = 0;
    private ArrayList<VarInfo> localVarInfo    = new ArrayList<VarInfo>();

    public LocalVarTable(Method method) {
        init(method);
    }

    /**
     * 设置函数入参的加载指令及slotpos
     */
    private void init(Method method) {
        nextFreeSlotPos = 0;
        localVarInfo.add(new VarInfo(0, ALOAD));// this
        nextFreeSlotPos++;
        Class<?>[] paramTypes = method.getParameterTypes();
        countOfArgs = 1 + paramTypes.length;
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].isPrimitive()) {
                    String pName = paramTypes[i].getName();
                    if ("int".equals(pName) || "boolean".equals(pName) || "short".equals(pName) || "byte".equals(pName) || "char".equals(pName)
                            || "boolean".equals(pName) || "boolean".equals(pName) || "boolean".equals(pName))
                        localVarInfo.add(new VarInfo(nextFreeSlotPos, ILOAD));
                    else if ("float".equals(pName))
                        localVarInfo.add(new VarInfo(nextFreeSlotPos, FLOAD));
                    else if ("double".equals(pName)) {
                        localVarInfo.add(new VarInfo(nextFreeSlotPos, DLOAD));
                        nextFreeSlotPos++;
                    } else if ("long".equals(pName)) {
                        localVarInfo.add(new VarInfo(nextFreeSlotPos, LLOAD));
                        nextFreeSlotPos++;
                    } else throw new RuntimeException("不支持的类型" + pName);
                } else {
                    localVarInfo.add(new VarInfo(nextFreeSlotPos, ALOAD));
                }
                nextFreeSlotPos++;
            }
        }
    }

    /**
     * 存放局部变量，修改nextFreeSlotPos
     * 
     * @param mv
     * @param clazz
     */
    public void setLocal(MethodVisitor mv, Class<?> clazz) {
        if (clazz.isPrimitive()) {
            String pName = clazz.getName();
            if ("int".equals(pName) || "boolean".equals(pName) || "short".equals(pName) || "byte".equals(pName) || "char".equals(pName)) {
                mv.visitVarInsn(ISTORE, nextFreeSlotPos);
                localVarInfo.add(new VarInfo(nextFreeSlotPos, ILOAD));
            } else if ("float".equals(pName)) {
                mv.visitVarInsn(FSTORE, nextFreeSlotPos);
                localVarInfo.add(new VarInfo(nextFreeSlotPos, FLOAD));
            } else if ("double".equals(pName)) {
                mv.visitVarInsn(DSTORE, nextFreeSlotPos);// 32位机，double/long占两个slot
                localVarInfo.add(new VarInfo(nextFreeSlotPos, DLOAD));
                nextFreeSlotPos++;
            } else if ("long".equals(pName)) {
                mv.visitVarInsn(LSTORE, nextFreeSlotPos);
                localVarInfo.add(new VarInfo(nextFreeSlotPos, LLOAD));
                nextFreeSlotPos++;
            } else throw new RuntimeException("不支持的类型" + clazz.getName());
        } else {
            mv.visitVarInsn(ASTORE, nextFreeSlotPos);
            localVarInfo.add(new VarInfo(nextFreeSlotPos, ALOAD));
        }
        nextFreeSlotPos++;
    }

    /**
     * 加载局部变量
     * 
     * @param mv
     * @param indexOfLocal
     */
    public void loadLocal(MethodVisitor mv, int indexOfLocal) {
        if (localVarInfo.size() >= indexOfLocal + countOfArgs) {
            VarInfo vinfo = localVarInfo.get(indexOfLocal + countOfArgs);
            mv.visitVarInsn(vinfo.loadOpcode, vinfo.slotPos);
        } else {
            throw new RuntimeException("无法获取尚未设置的局部变量,变量编号:" + indexOfLocal);
        }
    }

    /**
     * 加载函数入参
     * 
     * @param mv
     * @param indexOfArg
     */
    public void loadArg(MethodVisitor mv, int indexOfArg) {
        if (countOfArgs >= indexOfArg) {
            VarInfo vinfo = localVarInfo.get(indexOfArg);
            mv.visitVarInsn(vinfo.loadOpcode, vinfo.slotPos);
        } else {
            throw new RuntimeException("参数索引非法:" + indexOfArg);
        }
    }
}
