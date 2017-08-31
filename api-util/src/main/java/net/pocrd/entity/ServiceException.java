package net.pocrd.entity;

import java.io.Serializable;

/**
 * 限于 dubbo 接口声明时使用, 业务中请使用 BusinessException
 * Created by rendong on 14/11/4.
 */
public class ServiceException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    private int    code;
    private int    displayCode;
    // 展现给外部的错误信息
    private String description;
    // 内部日志使用的错误信息
    private String msg;

    private ServiceException() {
    }

    public ServiceException(AbstractReturnCode code) {
        this(code, code.getDesc());
    }

    public ServiceException(AbstractReturnCode code, String msg) {
        super("code:" + code.getCode() + " desc:" + code.getDesc() + " msg:" + msg);
        this.code = code.getCode();
        this.displayCode = code.getDisplay().getCode();
        this.description = code.getDisplay().getDesc();
        this.msg = msg;
    }

    public ServiceException(AbstractReturnCode code, Throwable t) {
        this(code, code.getDesc(), t);
    }

    public ServiceException(AbstractReturnCode code, String msg, Throwable t) {
        super("code:" + code.getCode() + " desc:" + code.getDesc() + " msg:" + msg, t);
        this.code = code.getCode();
        this.displayCode = code.getDisplay().getCode();
        this.description = code.getDisplay().getDesc();
        this.msg = msg;
    }

    public ServiceException(String msg, ServiceRuntimeException sre) {
        super("code:" + sre.getCode() + " desc:" + sre.getDescription() + " msg:" + msg, sre);
        this.code = sre.getCode();
        this.displayCode = sre.getDisplayCode();
        this.description = sre.getDescription();
        this.msg = sre.getMsg();
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
