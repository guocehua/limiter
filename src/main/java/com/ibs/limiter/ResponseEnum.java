package com.ibs.limiter;

public enum ResponseEnum {
    OK(200,"成功"),
    LIMIT(401,"访问受限"),
    FAILED(500,"失败");
    private int code;
    private String msg;
    ResponseEnum(int num,String msg){
        this.code=num;
        this.msg=msg;
    }
    public String getMsg(){
        return msg;
    }
}
