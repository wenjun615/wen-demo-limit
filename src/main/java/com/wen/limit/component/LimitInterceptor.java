package com.wen.limit.component;

import com.wen.limit.entity.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 拦截器
 * </p>
 *
 * @author wenjun
 * @since 2022-06-29
 */
@Component
public class LimitInterceptor extends HandlerInterceptorAdapter {

    ConcurrencyLimit concurrencyLimit;

    public LimitInterceptor() {
        this.concurrencyLimit = ConcurrencyLimit.create(10);
    }

    /**
     * 调用者快速返回，不会一直等待
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!concurrencyLimit.tryAcquire()) {
            response.getWriter().println("ERROR");
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        concurrencyLimit.release();
    }
}
