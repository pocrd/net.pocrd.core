package net.pocrd.entity;

import java.io.Serializable;

/**
 * 封装与 error code 相关的业务异常
 * Created by rendong on 14/11/15.
 */
public class ServiceRuntimeException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private int    code;
    private int    displayCode;
    // 展现给外部的错误信息
    private String description;
    // 内部日志使用的错误信息
    private String msg;

    private ServiceRuntimeException() {
    }

    public ServiceRuntimeException(AbstractReturnCode code) {
        this(code, code.getDesc());
    }

    public ServiceRuntimeException(AbstractReturnCode code, String msg) {
        super("code:" + code.getDesc() + ":" + code.getCode() + "msg:" + msg);
        this.code = code.getCode();
        this.displayCode = code.getDisplay().getCode();
        this.description = code.getDisplay().getDesc();
        this.msg = msg;
    }

    public ServiceRuntimeException(AbstractReturnCode code, Throwable t) {
        this(code, code.getDesc(), t);
    }

    public ServiceRuntimeException(AbstractReturnCode code, String msg, Throwable t) {
        super("code:" + code.getDesc() + ":" + code.getCode() + "msg:" + msg, t);
        this.code = code.getCode();
        this.displayCode = code.getDisplay().getCode();
        this.description = code.getDisplay().getDesc();
        this.msg = msg;
    }

    public ServiceRuntimeException(String msg, ServiceException se) {
        super("code:" + se.getDescription() + ":" + se.getCode() + "msg:" + msg, se);
        this.code = se.getCode();
        this.displayCode = se.getDisplayCode();
        this.description = se.getDescription();
        this.msg = se.getMsg();
    }

    /*
     * 内部使用code
     */
    public int getCode() {
        return code;
    }

    /*
     * 对外显示code
     */
    public int getDisplayCode() {
        return displayCode;
    }

    /*
     * 内部使用message
     */
    public String getMsg() {
        return msg;
    }

    /*
     * 对外显示message
     */
    public String getDescription() {
        return description;
    }
}