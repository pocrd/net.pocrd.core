package net.pocrd.entity;

import net.pocrd.define.SecurityType;

import java.io.Serializable;

/**
 * 调用者信息, 被密文传输
 *
 * @author rendong
 */
public class CallerInfo implements Serializable {

    public static final CallerInfo TESTER = new CallerInfo();

    // 参与token计算
    public int    appid;                    // 终端类型
    public int    securityLevel;            // 授权级别
    public long   expire;                   // 过期时间
    public long   deviceId;                 // 设备编号
    public long   uid;                      // 用户编号
    public byte[] key;                      // 设备身份公钥
    public int    subSystemId;                // 子系统名称(可选)
    public String subSystemRole;            // 子系统角色(可选)
    public long subSystemMainId = Long.MIN_VALUE;          // 子系统主键(可选)

    static {
        if (CompileConfig.isDebug) {
            TESTER.deviceId = -1;
            TESTER.uid = 1234567890909L;
            TESTER.expire = Long.MAX_VALUE;
            TESTER.securityLevel = SecurityType.Test.authorize(0);
        }
    }
}
