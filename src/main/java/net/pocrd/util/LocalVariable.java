package net.pocrd.util;

public class LocalVariable {
    public LocalVariable(int slotPos, int loadOpcode, int storeOpcode) {
        this.slotPos = slotPos;
        this.loadOpcode = loadOpcode;
        this.storeOpcode = storeOpcode;
    }

    /**
     * 加载变量的指令
     */
    private int loadOpcode;
    /**
     * 存储变量的指令
     */
    private int storeOpcode;
    /**
     * 变量在局部变量表中的存储位置
     */
    private int slotPos;

    public int getLoadOpcode() {
        return loadOpcode;
    }

    public void setLoadOpcode(int loadOpcode) {
        this.loadOpcode = loadOpcode;
    }

    public int getStoreOpcode() {
        return storeOpcode;
    }

    public void setStoreOpcode(int storeOpcode) {
        this.storeOpcode = storeOpcode;
    }

    public int getSlotPos() {
        return slotPos;
    }

    public void setSlotPos(int slotPos) {
        this.slotPos = slotPos;
    }
}