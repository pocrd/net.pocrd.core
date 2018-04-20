package net.pocrd.define;

public enum SecurityType {

    /**
     * 测试用, 只对内网环境访问开放此权限
     */
    Test(-1),

    /**
     * 无认证, 用户无关的接口. eg. 无任何安全风险的接口
     */
    None(0x0000),

    /**
     * 带有该标识的token只在ssl信道中传递, 用于处理跨domain的csrftoken同步. 该安全级别不能用于访问任何接口
     */
    SeceretUserToken(0x0001),

    /**
     * 第三方平台OAuth认证, token中携带平台名和平台id
     * 验证要素
     * 1. 包含三方平台信息的设备token
     * 2. 设备签名
     */
    OAuthVerified(0x0002),

    /**
     * 设备认证, 验证设备签名. eg. 有一定安全风险但与用户无关的接口(发送下行短信密码)
     * 验证要素
     * 1. 设备token
     * 2. 设备签名
     */
    RegisteredDevice(0x0010),

    /**
     * 用户认证，已拥有用户id 但尚未完成用户注册的完整流程(未绑定手机号或其他原因)
     * 验证要素
     * 1. 用户token
     * 2. 设备签名
     */
    User(0x0020),

    /**
     * 用户登录认证, 验证用户名密码, 且验证设备受信并激活
     * 验证要素
     * 1. 用户token 包含设备token
     * 2. 设备签名
     * 3. 后台验证用户与设备之间存在授信
     * 4. 后台验证设备与用户的关系处于激活状态
     */
    UserLogin(0x0100),

    /**
     * 用户授权, 通过短信认证码等方式获得高等级临时授权, 建议授权持续时间不超过60秒, 并可在业务系统间做计数
     */
    UserAuth(0x0200),

    /**
     * 内部用户认证，
     * 验证要素
     * 1. 设备签名
     * 2. 用户token
     * 3. 内网ip
     */
    InternalUser(0x0400),

    /**
     * 子系统用户认证. eg. 供应商系统用户
     * 验证要素
     * 1. 设备签名
     * 2. 用户token
     * 3. 子系统标识
     */
    SubSystemUser(0x1000),

    /**
     * 第三方集成认证, 验证第三方证书签名
     */
    Integrated(0x00100000),

    /**
     * 内网环境验证
     */
    Internal(0x00200000),

    /**
     * 对于只需要生成文档不产生代理的接口,设置本安全级别(本安全级别无业务含义)
     */
    Document(0xF0000000);

    private int code;

    /**
     * @param code security16进制编码
     */
    private SecurityType(int code) {
        this.code = code;
    }

    /**
     * 检查auth权限是否包含当前权限
     */
    public boolean check(int auth) {
        return (auth & code) == code;
    }

    /**
     * 检查当前权限是否包含auth权限
     */
    public boolean contains(int auth) {
        return (auth & code) != 0;
    }

    /**
     * 检查auth权限是否包含当前权限
     */
    public boolean check(SecurityType auth) {
        return (auth.code & code) == code;
    }

    /**
     * 在auth权限的基础上增加当前权限
     */
    public int authorize(int auth) {
        return auth | this.code;
    }

    /**
     * 判断auth权限是否为空
     */
    public static boolean isNone(int auth) {
        return auth == 0;
    }

    private static final int EXPIRABLE = OAuthVerified.code | User.code | UserLogin.code | SeceretUserToken.code | SubSystemUser.code;

    /**
     * 判断auth是否会过期, 包含 OAuthVerified, User, UserLogin, SeceretUserToken, SubSystemUser
     * 其中之一的auth都可能会过期
     */
    public static boolean expirable(int auth) {
        return (auth & EXPIRABLE) != 0;
    }

    private static final int TOKEN_REQUIRED = RegisteredDevice.code | OAuthVerified.code | User.code | UserLogin.code | SubSystemUser.code;

    /**
     * 判断auth是否需要验证token, 包含 OAuthVerified, RegisteredDevice, User, UserLogin, SubSystemUser
     */
    public static boolean requireToken(int auth) {
        return (auth & TOKEN_REQUIRED) != 0;
    }
}
