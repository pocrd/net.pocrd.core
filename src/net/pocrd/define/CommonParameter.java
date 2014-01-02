package net.pocrd.define;

public enum CommonParameter {
    /**
     * Format
     * 返回值格式，取值为枚举SerializeType中的定义
     */
    ft,
    
    /**
     * Location
     * 用于返回信息国际化
     */
    lo,
    
    /**
     * token
     * 代表访问者身份
     */
    tk,
    
    /**
     * method
     * 请求的资源名
     */
    mt,
    
    /**
     * signature
     * 参数字符串签名
     */
    sig,
    
    /**
     * application id
     * 应用编号
     */
    aid,
    
    /**
     * call id
     * 客户端调用编号
     */
    cid,
    
    /**
     * device id
     * 设备标示符
     */
    did,
    
    /**
     * version code
     * 客户端数字版本号
     */
    vc,
}
