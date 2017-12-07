package com.lilj.analysis.exception;

/**
 * Created by lilj on 2017/5/17.
 */
public class ConstructException extends Exception {

    private String message;
    private String construct;
    private Throwable cause = this;

    public ConstructException(String message) {
        this.message = message;
    }

    public ConstructException(String message, Throwable e) {
        this.message = message;
        this.cause = e;
    }

    public ConstructException(String message, String construct, Throwable e) {
        this.message = message;
        this.construct = construct;
        this.cause = e;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConstruct() {
        return construct;
    }

    public void setConstruct(String construct) {
        this.construct = construct;
    }
}
