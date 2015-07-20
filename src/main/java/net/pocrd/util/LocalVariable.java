package net.pocrd.util;

class LocalVariable {
    LocalVariable(int slotPos, int loadOpcode, int storeOpcode) {
        this.slotPos = slotPos;
        this.loadOpcode = loadOpcode;
        this.storeOpcode = storeOpcode;
        this.doubleSlot = false;
    }

    LocalVariable(int slotPos, int loadOpcode, int storeOpcode, boolean doubleSlot) {
        this.slotPos = slotPos;
        this.loadOpcode = loadOpcode;
        this.storeOpcode = storeOpcode;
        this.doubleSlot = doubleSlot;
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

    /**
     * 是否占用两个slot
     */
    private boolean doubleSlot;

    public int getLoadOpcode() {
        return loadOpcode;
    }

    public int getStoreOpcode() {
        return storeOpcode;
    }

    public int getSlotPos() {
        return slotPos;
    }

    public boolean isDoubleSlot() {return doubleSlot;}
}