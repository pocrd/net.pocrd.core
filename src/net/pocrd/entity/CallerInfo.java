package net.pocrd.entity;

import net.pocrd.define.SecurityType;

public class CallerInfo {
    public static final CallerInfo ANONYMOUS;
    static {
        ANONYMOUS = new CallerInfo();
        ANONYMOUS.securityLevel = SecurityType.None;
    }

    // 参与token计算
    public String                  sn;
    public String                  uid;
    public String                  appid;
    public int                     level;
    public long                    expire;
    public SecurityType            securityLevel;

    public String[]                groups;
}
