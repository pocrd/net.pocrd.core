package net.pocrd.define;

public enum CommonParameter {
    /**
     * 客户端功能性版本号，该参数可定位到客户端支持的功能集
     */
    ver,
    
    /**
     * 返回值格式，取值为枚举SerializeType中的定义
     */
    format,
    
    /**
     * 用于返回信息国际化
     */
    lo,
    
    /**
     * 代表访问者身份
     */
    token,
    
    /**
     * 请求的资源名
     */
    method,
    
    /**
     * 参数字符串签名
     */
    sig,
}
