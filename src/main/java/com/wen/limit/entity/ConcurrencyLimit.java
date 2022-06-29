package com.wen.limit.entity;

import java.util.concurrent.Semaphore;

/**
 * <p>
 * 并发限流
 * </p>
 *
 * @author wenjun
 * @since 2022-06-29
 */
public class ConcurrencyLimit {

    private Semaphore semaphore;

    private ConcurrencyLimit() {
    }

    public static ConcurrencyLimit create(int permits) {
        ConcurrencyLimit concurrencyLimit = new ConcurrencyLimit();
        concurrencyLimit.semaphore = new Semaphore(permits);
        return concurrencyLimit;
    }

    public void acquire() throws InterruptedException {
        this.semaphore.acquire();
    }

    public void release() {
        this.semaphore.release();
    }

    public boolean tryAcquire() {
        return this.semaphore.tryAcquire();
    }
}
