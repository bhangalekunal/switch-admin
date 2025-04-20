package com.codemaster.switchadmin.exception;

public class AppConfigAlreadyExistsException extends RuntimeException{

    public AppConfigAlreadyExistsException() {
        super();
    }

    public AppConfigAlreadyExistsException(String message) {
        super(message);
    }

    public AppConfigAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppConfigAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected AppConfigAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
