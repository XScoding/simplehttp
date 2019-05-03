package com.xs.simplehttp.core;

/**
 * post type
 *
 * Created by xs code on 2019/3/12.
 */

public enum PostType {

    JSON("application/json;charset=utf-8"),
    FORM("application/x-www-form-urlencoded");

    private String type;

    PostType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
