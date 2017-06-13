package net.pocrd.core;

/**
 * 序列化异常
 */
public class SerializeException extends Exception {
    private Exception exception;
    public SerializeException(Exception exception) {
        this.exception = exception;
    }
    public Exception getException() {
        return exception;
    }
}
