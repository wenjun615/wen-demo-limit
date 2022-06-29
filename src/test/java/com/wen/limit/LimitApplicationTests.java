package com.wen.limit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wen.limit.entity.ConcurrencyLimit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class LimitApplicationTests {

    /**
     * Semaphore 限流测试
     */
    public static void main(String[] args) {
        // 同一时刻最多 5 个线程
        ConcurrencyLimit limit = ConcurrencyLimit.create(5);
        ExecutorService executorService = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                        .setNameFormat("limit-%d")
                        .build());
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    limit.acquire();
                    System.out.println(Thread.currentThread().getName() + " START");
                    // 模拟内部耗时
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + " END");
                    limit.release();
                }
            });
        }
    }

    @Test
    void contextLoads() {

    }

}
