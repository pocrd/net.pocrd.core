package net.pocrd.entity;

public class ReturnCodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ReturnCode        code;

    public ReturnCodeException(ReturnCode code) {
        super("code" + code.getCode());
        this.code = code;
    }
    
    public ReturnCodeException(ReturnCode code, Exception e) {
        super("code" + code.getCode(), e);
        this.code = code;
    }

    public ReturnCode getCode() {
        return code;
    }
}
