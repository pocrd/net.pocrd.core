package net.pocrd.define;

public enum SecurityType {
    /**
     * 测试用
     */
    Test(-1),
    
    /**
     * 无认证
     */
    None(0x00),

    /**
     * 设备认证
     */
    SNValide(0x01),

    /**
     * 用户静态密码认证
     */
    UserStatic(0x02),

    /**
     * 用户动态密码认证
     */
    UserDynamic(0x04),

    /**
     * 第三方集成认证
     */
    Integrated(0x08),

    /**
     * 内部服务端认证
     */
    Server(0x10);

    private int code;

    private SecurityType(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
