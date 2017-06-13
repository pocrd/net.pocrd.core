package net.pocrd.entity;

/**
 * byte code 中使用到
 */
public class ReturnCodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private AbstractReturnCode code;

    public ReturnCodeException(AbstractReturnCode code) {
        super("code:" + code.getName() + ":" + code.getCode());
        this.code = code;
    }

    public ReturnCodeException(AbstractReturnCode code, String msg) {
        super("code:" + code.getName() + ":" + code.getCode() + "msg:" + msg);
        this.code = code;
    }

    public ReturnCodeException(AbstractReturnCode code, Exception e) {
        super("code:" + e.toString() + code.getName() + ":" + code.getCode(), e);
        this.code = code;
    }

    public ReturnCodeException(AbstractReturnCode code, String msg, Exception e) {
        super("code:" + e.toString() + code.getName() + ":" + code.getCode() + "msg:" + msg, e);
        this.code = code;
    }

    public AbstractReturnCode getCode() {
        return code;
    }
}
