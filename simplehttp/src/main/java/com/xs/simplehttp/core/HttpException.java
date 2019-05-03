package com.xs.simplehttp.core;

/**
 * exception extension
 *
 * Created by xs code on 2019/3/12.
 */

public class HttpException extends Exception {

    private int code;

    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
