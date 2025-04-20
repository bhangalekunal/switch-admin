package com.codemaster.switchadmin.exception;

public class UserAccountNotFoundException extends RuntimeException{
    public UserAccountNotFoundException(String message) {
        super(message);
    }

    public UserAccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAccountNotFoundException(Throwable cause) {
        super(cause);
    }

    protected UserAccountNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserAccountNotFoundException() {
        super();
    }
}
