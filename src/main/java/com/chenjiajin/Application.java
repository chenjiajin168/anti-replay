package com.chenjiajin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author com.chenjiajin
 * @date 2022/2/16
 */
@SpringBootApplication
@MapperScan(basePackages = "com.chenjiajin.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
