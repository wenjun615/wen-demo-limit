package com.wen.limit.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.wen.limit.annotation.Limit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Guava 限流
 * </p>
 *
 * @author wenjun
 * @since 2022-06-27
 */
@Slf4j
@RestController
@RequestMapping("/limit")
public class LimitController {

    /**
     * 限流策略：1 秒 1 次请求
     */
    private final RateLimiter limiter = RateLimiter.create(1.0);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/test1")
    public String test1() {
        // 指定 500 毫秒内获取令牌，如果不能返回 false
        boolean tryAcquire = limiter.tryAcquire(500, TimeUnit.MILLISECONDS);
        if (!tryAcquire) {
            log.warn("进入服务降级，时间：{}", LocalDateTime.now().format(formatter));
            return "当前排队人数较多，请稍后再试";
        }
        log.info("获取令牌成功，时间：{}", LocalDateTime.now().format(formatter));
        return "请求成功";
    }

    @GetMapping("/test2")
    @Limit(key = "limit", permitsPerSecond = 1, timeout = 500, msg = "当前排队人数较多，请稍后再试")
    public String test2() {
        log.info("令牌桶limit获取令牌成功");
        return "ok";
    }
}
