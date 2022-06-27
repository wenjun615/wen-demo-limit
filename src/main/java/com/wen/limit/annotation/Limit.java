package com.wen.limit.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 限流注解
 * </p>
 *
 * @author wenjun
 * @since 2022-06-27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Limit {

    /**
     * 令牌桶的 key
     */
    String key() default "";

    /**
     * 每秒限制访问次数
     */
    double permitsPerSecond();

    /**
     * 获取令牌最大等待时间
     */
    long timeout();

    /**
     * 获取令牌最大等待时间单位，默认：毫秒
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    /**
     * 未获取到令牌提示消息
     */
    String msg() default "系统繁忙，请稍后再试";
}