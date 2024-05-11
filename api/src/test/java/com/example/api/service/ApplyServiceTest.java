package com.example.api.service;

import com.example.api.repository.CouponRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    public void 한번만응모() {
        applyService.apply(1L);

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명응모() throws InterruptedException {
        int threadCount = 1000;  // 실제와 유사한 환경을 모방하도록 스레드 수 조정

        // multiThread (병렬 작업을 도와주는 Java api)
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 연결 풀 크기에 맞춘 스레드 풀 크기

        // 모든 요청이 끝날때 까지 기다림 ( 다른 스레드에서 수행하는 작업을 기다려주도록 도와주는 class )
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  // 모든 스레드의 작업 완료를 기다림

        executorService.shutdown();  // 스레드 풀 종료

        long count = couponRepository.count();  // 모든 적용이 완료된 후에 쿠폰 수 검사

        assertThat(count).isEqualTo(100);  // 비즈니스 로직에서 제한했던 쿠폰 응모수 100 개와 일치하는지 검증
    }

}