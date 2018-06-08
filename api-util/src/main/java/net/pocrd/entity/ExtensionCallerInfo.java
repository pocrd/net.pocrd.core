package net.pocrd.entity;

import java.util.HashMap;

/**
 * 调用者扩展信息, 被明文传输, 并由子系统签名
 *
 * @author rendong
 */
public class ExtensionCallerInfo {
    public long userid; //用户编号
    public int  subSystem; //子系统编号
    public long expired; // 过期时间
    public long deviceId; // 设备号

    public HashMap<String, String> info; // 扩展信息

}
