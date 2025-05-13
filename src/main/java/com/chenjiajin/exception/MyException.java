package com.chenjiajin.exception;

/**
 * @author chenjiajin
 * @date 2022/8/22
 */
public class MyException extends RuntimeException {


    /**
     * 构造函数不需要@Override注解
     */
    public MyException(String message) {
        super(message);
    }

}
