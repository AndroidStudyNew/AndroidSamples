package com.sjtu.charles.base;

/**
 * Created by CharlesZhu on 2016/4/14.
 */
public class PostException extends BaseException{

    public PostException(Throwable throwable) {
        super(throwable);
        this.code = CODE_ERROR_OPENCONN;
    }
}
