package net.pocrd.define;

import net.pocrd.entity.CompileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SecurityType {

    /**
     * 测试用, 只对内网环境访问开放此权限
     */
    Test(-1),

    /**
     * 无认证, 用户无关的接口. eg. 无任何安全风险的接口
     */
    None(0x00),

    /**
     * 带有该标识的token只在ssl信道中传递, 用于处理跨domain的csrftoken同步. 该安全级别不能用于访问任何接口
     */
    SeceretUserToken(0x01),

    /**
     * 设备认证, 验证设备签名. eg. 有一定安全风险但与用户无关的接口(发送下行短信密码)
     * 验证要素
     * 1. 设备token
     * 2. 设备签名
     */
    RegisteredDevice(0x0100),

    /**
     * 用户认证, 验证用户token, 不建议使用这个安全级别, 用户相关请使用 UserLogin
     * 验证要素
     * 1. 用户token
     * 2. 设备签名
     */
    User(0x0200),

    /**
     * 受信设备认证, 验证服务端存在设备ID到用户ID的信任绑定关系. 暂无实例使用这个安全级别.
     * 验证要素
     * 1. 用户token 包含设备token
     * 2. 设备签名
     * 3. 后台验证用户与设备之间存在授信
     */
    UserTrustedDevice(0x0400),

    /**
     * 手机动态密码认证, 验证手机号的动态密码. eg. 动态密码注册
     * 验证要素
     * 1. 手机号
     * 2. 短信验证码
     * 3. 设备token
     * 4. 设备签名
     */
    MobileOwner(0x0800),

    /**
     * 同时验证受信设备和手机验证码, 并验证手机号所代表的用户与设备的受信关系. eg. 用户非登录态重置密码
     * 验证要素
     * 1. 手机号      可倒查出userId
     * 2. 短信验证码
     * 3. 设备token
     * 4. 设备签名
     * 5. 倒查出的userId与设备是受信关系
     */
    MobileOwnerTrustedDevice(0x1000),

    /**
     * 用户登录认证, 验证用户名密码, 且验证设备受信并激活
     * 验证要素
     * 1. 用户token 包含设备token
     * 2. 设备签名
     * 3. 后台验证用户与设备之间存在授信
     * 4. 后台验证设备与用户的关系处于激活状态
     */
    UserLogin(0x2000),

    /**
     * 复合认证(UserLogin|MobileOwner): 同时进行静态密码和动态密码的验证(不验证手机号与用户的关联性), eg. 绑定手机号到当前用户(未绑定过手机号)
     */
    UserLoginAndMobileOwner(UserLogin.code | MobileOwner.code),

    /**
     * 第三方集成认证, 验证第三方证书签名
     */
    Integrated(0x10000000),

    /**
     * 内网环境验证
     */
    Internal(0x20000000),

    /**
     * 对于只需要生成文档不产生代理的接口,设置本安全级别(本安全级别无业务含义)
     */
    Document(0x40000000);

    private int     code;
    private boolean needUserToken;
    private static final Logger logger = LoggerFactory.getLogger(SecurityType.class);

    /**
     * @param code security16进制编码
     */
    private SecurityType(int code) {
        this.code = code;
        this.needUserToken = needUserToken;
    }

    /**
     * 检查auth权限是否包含当前权限
     */
    public boolean check(int auth) {
        return (auth & code) == code;
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
}
