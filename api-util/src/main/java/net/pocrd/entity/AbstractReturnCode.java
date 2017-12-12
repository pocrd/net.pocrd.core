package net.pocrd.entity;

import java.io.Serializable;

/**
 * Created by sunji on 2014/7/24.
 * Add private String service.
 */
public abstract class AbstractReturnCode implements Serializable {

    private       String name;
    private final String desc;
    private final int    code;

    private       String             service;
    private final AbstractReturnCode display;

    /**
     * 初始化一个对外暴露的ReturnCode(用于客户端异常处理)
     */
    public AbstractReturnCode(String desc, int code) {
        this.desc = desc;
        this.code = code;
        this.display = this;
    }

    /**
     * 初始化一个不对外暴露的ReturnCode(仅用于服务端数据分析)
     */
    public AbstractReturnCode(int code, AbstractReturnCode displayAs) {
        this.desc = null;
        this.code = code;
        this.display = displayAs;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public AbstractReturnCode getDisplay() {
        return display;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
