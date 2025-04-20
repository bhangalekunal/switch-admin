package com.codemaster.switchadmin.exception;

public class AppConfigNotFoundException extends RuntimeException{

    public AppConfigNotFoundException() {
        super();
    }

    public AppConfigNotFoundException(String message) {
        super(message);
    }

    public AppConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppConfigNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AppConfigNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
