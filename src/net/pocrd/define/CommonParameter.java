package net.pocrd.define;

public enum CommonParameter {
    /**
     * FunctionFlags
     * 客户端功能性标签，用于标示客户端支持的扩展功能
     */
    ff,
    
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
    si,
}
