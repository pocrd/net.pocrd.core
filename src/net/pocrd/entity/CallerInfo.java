package net.pocrd.entity;

import net.pocrd.define.SecurityType;
import net.pocrd.util.CommonConfig;

public class CallerInfo {

    public static final CallerInfo TESTER = new CallerInfo();
    static {
        if (CommonConfig.isDebug) {
            TESTER.sn = "123456";
            TESTER.uid = "tester";
            TESTER.appid = "T";
            TESTER.level = Integer.MAX_VALUE;
            TESTER.expire = Long.MAX_VALUE;
            TESTER.securityLevel = SecurityType.Test.getValue();
        }
    }

    public String                  key;
    public String[]                groups;

    // 参与token计算
    public String                  sn;
    public String                  uid;
    public String                  appid;
    public int                     level;
    public long                    expire;
    public int                     securityLevel;
}
