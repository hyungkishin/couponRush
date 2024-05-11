package com.example.api.service;

import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.AppliedUserRepository;
import com.example.api.repository.CouponCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ApplyService {

    private final CouponCountRepository couponCountRepository;

    private final CouponCreateProducer couponCreateProducer;

    private final AppliedUserRepository appliedUserRepository;

    @Transactional
    public void apply(Long userId) {
        Long apply = appliedUserRepository.add(userId);

        // 추가된 갯수가 1이 아닌경우, 이미 쿠폰을 발급 받은 사용자라 간주하고 비즈니스 로직을 수행하지 않는다.
        if (apply != 1) {
            return;
        }

        // 쿠폰 최초 발급 유저라 간주하고 발급 은 진행한다.
        Long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }

}
