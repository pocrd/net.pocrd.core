package net.pocrd.entity;


public class ApiParameterInfo {
    /**
     * 参数类型
     */
    public Class<?> type;

    /**
     * 默认值字符串形式
     */
    public String  defaultValue;
    
    /**
     * 验证字符串表达式
     */
    public String verifyRegex;
    
    /**
     * 验证错误提示
     */
    public String verifyMsg;
    
    /**
     * 是否必须
     */
    public boolean isRequired;

    /**
     * 参数名
     */
    public String  name;

    /**
     * 参数描述
     */
    public String  description;
}
