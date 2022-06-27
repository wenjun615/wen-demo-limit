package com.wen.limit.aop;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.wen.limit.annotation.Limit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 限流切面
 * </p>
 *
 * @author wenjun
 * @since 2022-06-27
 */
@Slf4j
@Aspect
@Component
public class LimitAop {

    /**
     * 令牌桶 Map
     */
    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    @Around("@annotation(com.wen.limit.annotation.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取 @Limit 注解信息
        Limit limit = method.getAnnotation(Limit.class);
        if (Objects.nonNull(limit)) {
            String key = limit.key();
            RateLimiter limiter;
            if (!limitMap.containsKey(key)) {
                limiter = RateLimiter.create(limit.permitsPerSecond());
                limitMap.put(key, limiter);
                log.info("新建了令牌桶={}，容量={}", key, limit.permitsPerSecond());
            }
            limiter = limitMap.get(key);
            boolean acquire = limiter.tryAcquire(limit.timeout(), limit.timeunit());
            if (!acquire) {
                log.warn("令牌桶={}，获取令牌失败", key);
                this.responseFail(limit.msg());
                return null;
            }
        }
        return joinPoint.proceed();
    }

    private void responseFail(String msg) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
        response.setContentType("application/json;charset=UTF-8");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
