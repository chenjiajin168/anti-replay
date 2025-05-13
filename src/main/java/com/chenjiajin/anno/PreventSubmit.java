package com.chenjiajin.anno;

import java.lang.annotation.*;

/**
 * 防重注解
 */

@Target({ElementType.METHOD})           // 标识该注解用于方法上
@Retention(RetentionPolicy.RUNTIME)     // 申明该注解为运行时注解，编译后改注解不会被遗弃
@Documented                             //javadoc工具记录
public @interface PreventSubmit {
}

