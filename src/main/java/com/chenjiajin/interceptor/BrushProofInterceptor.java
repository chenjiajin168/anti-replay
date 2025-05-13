package com.chenjiajin.interceptor;

import com.chenjiajin.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 接口防刷 拦截器
 *
 * @author chenjiajin
 */
@Component
@Slf4j
public class BrushProofInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate template;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 防刷验证
        String url = request.getRequestURI().substring(1);  // 去除第一个字符"/"
        String ip = RequestUtil.getIPAddress();
        log.info("ip:{} 访问: {}", ip, url);

        String key = RequestUtil.join(url, ip);
        if (!this.isAllowBrush(key)) {
            response.setContentType("text/json;charset=UTF-8");
            response.getWriter().write("请勿频繁访问, 谢谢咯");
            return false;
        }

        return true;
    }

    /**
     * 判断某个ip对应某个接口的访问次数 (接口防刷)
     * 十秒内能访问5次
     * false:超频
     * true:未超频
     */
    private boolean isAllowBrush(String key) {
        // 如果有不做 任何操作，如果没有添加
        template.opsForValue().setIfAbsent(key, "1", 3, TimeUnit.SECONDS);
        Long decrement = template.opsForValue().decrement(key);
        return decrement != null && decrement >= 0;
    }


}
