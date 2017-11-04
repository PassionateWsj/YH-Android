package com.intfocus.spy_template.general.net;

/**
 * Created by admin on 2016/6/30.
 */
public class ResultException extends RuntimeException {
    private int errorCode;

    public ResultException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
