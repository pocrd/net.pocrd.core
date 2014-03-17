package net.pocrd.define;

public enum SecurityType {
    /**
     * 测试用, 只对内网访问开放此权限
     */
    Test(-1),
    
    /**
     * 无认证
     */
    None(0x00),

    /**
     * 设备认证, 验证设备签名
     */
    Device(0x01),

    /**
     * 用户静态密码认证, 验证用户名密码
     */
    UserStatic(0x02),

    /**
     * 用户动态密码认证, 验证用户相关的动态密码
     */
    UserDynamic(0x04),

    /**
     * 第三方集成认证, 验证第三方证书签名
     */
    Integrated(0x08),

    /**
     * 内部服务端认证, 验证ip和应用签名
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
