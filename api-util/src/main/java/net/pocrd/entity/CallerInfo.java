package net.pocrd.entity;

import net.pocrd.define.SecurityType;

import java.io.Serializable;

/**
 * 调用者信息，包括设备信息和用户信息(已登录)的一部分
 *
 * @author rendong
 */
public class CallerInfo implements Serializable {

    public static final CallerInfo TESTER = new CallerInfo();

    static {
        if (CompileConfig.isDebug) {
            TESTER.deviceId = -1;
            TESTER.uid = 1234567890909L;
            TESTER.expire = Long.MAX_VALUE;
            TESTER.securityLevel = SecurityType.Test.authorize(0);
        }
    }

    // 参与token计算
    public int    appid;
    public int    securityLevel;
    public long   expire;
    public long   deviceId;
    public long   uid;
    public String oauthid;
    public byte[] key;                      // 设备身份公钥
    public String role;
}
