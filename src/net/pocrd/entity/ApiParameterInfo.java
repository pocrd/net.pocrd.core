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
