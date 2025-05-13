package com.chenjiajin.anno;


import com.chenjiajin.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 防重 AOP
 *
 * @date 2020/8/12
 */
@Component
@Aspect
@Slf4j
public class PreventSubmitAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    private static final String API_PREVENT_SUBMIT = "api:preventSubmit:";  // 放重redis前缀

    private static final String API_LOCK_PREVENT_SUBMIT = "api:preventSubmit:lock:";   // 放重分布式锁前缀

    private static final Integer INVALID_NUMBER = 3;  // 失效时间

    /**
     * 防重
     */
    @Around("@annotation(com.chenjiajin.anno.PreventSubmit)")
    public Object preventSubmitAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        RLock lock = null;

        try {

            Object[] args = joinPoint.getArgs();
            log.info("目标方法的参数 = {}", args);

            // 获取当前request请求
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

            String requestUri = request.getRequestURI().substring(1);  // 去除第一个字符"/"
            log.info("获取请求地址 = {}", requestUri);

            // 获取用户ID
            // Long userId = TokenUtil.getUserId(request);

            String join = String.join(":", Arrays.stream(args).map(s -> (String) s).collect(Collectors.toList()));

            // 拼接锁前缀，采用同一方法，同一用户,同一接口
            String temp = requestUri.concat(":").concat(join);

            log.info("放重分布式锁 = 采用同一方法，同一用户,同一接口 = {}", temp);

            // 拼接rediskey
            String lockPrefix = API_LOCK_PREVENT_SUBMIT.concat(temp);
            log.info("放重分布式锁 = {}", lockPrefix);
            String redisPrefix = API_PREVENT_SUBMIT.concat(temp);
            log.info("放重redis标记 = {}", redisPrefix);

            /**
             * 对同一方法同一用户同一参数加锁,即使获取不到用户ID,每个用户请求数据也会不一致，不会造成接口堵塞
             */
            lock = this.redissonClient.getLock(lockPrefix);
            lock.lock();

            String flag = this.stringRedisTemplate.opsForValue().get(redisPrefix);
            if (StringUtils.isNotEmpty(flag)) {
                log.info("拦截访问");
                throw new MyException("您当前的操作太频繁了,请稍后再试!");
            }

            // 存入redis,设置失效时间
            this.stringRedisTemplate.opsForValue().set(redisPrefix, redisPrefix, INVALID_NUMBER, TimeUnit.SECONDS);

            // 执行目标方法
            return joinPoint.proceed(args);

        } finally {
            if (lock != null) {
                log.info("释放锁");
                lock.unlock();
            }
        }

    }

}