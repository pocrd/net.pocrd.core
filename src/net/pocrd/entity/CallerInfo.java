package net.pocrd.entity;

import net.pocrd.define.SecurityType;
import net.pocrd.util.CommonConfig;

/**
 * 调用者信息，包括设备信息和用户信息(已登录)的一部分
 * @author rendong
 *
 */
public class CallerInfo {

    public static final CallerInfo TESTER = new CallerInfo();
    static {
        if (CommonConfig.isDebug) {
            TESTER.sn = -1;
            TESTER.uid = -1;
            TESTER.appid = -1;
            TESTER.level = Integer.MAX_VALUE;
            TESTER.expire = Long.MAX_VALUE;
            TESTER.securityLevel = SecurityType.Test.getValue();
        }
    }

    // 设备身份公钥
    public String                  key;

    // 设备/用户分组
    public String[]                groups;

    // 参与token计算
    public int                     appid;
    public long                    expire;
    public int                     level;
    public int                     securityLevel;
    public long                    sn;
    public long                    uid;
}
