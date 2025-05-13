package com.chenjiajin.config;

import com.chenjiajin.interceptor.BrushProofInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 使用自定义的拦截器 配置类
 */
@Configuration
public class InterceptorConfigurer implements WebMvcConfigurer {

    @Autowired
    private BrushProofInterceptor brushProofInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 防刷接口 拦截器
        registry.addInterceptor(brushProofInterceptor)
                .excludePathPatterns("/aop2")  // 排除aop2
                .addPathPatterns("/**");  // 配置拦截路径

    }


}
