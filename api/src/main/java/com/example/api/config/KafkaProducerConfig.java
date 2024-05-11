package com.example.api.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

@Configuration
public class KafkaProducerConfig {

    /**
     * producerFactory 를 생성하기 위한 메서드를 생성
     * spring 이 제공해준다.
     * @return
     */
    @Bean
    public ProducerFactory<String, Long> producerFactory() {
        // 설정 값을 담기 위해 Map 을 선언한다.
        HashMap<String, Object> config = new HashMap<>();

        // 서버 정보 추가
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Key serializer 정보를 추가
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // value serializer 정보를 추가
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);

        // 메시지를 보낸 후 모든 브로커의 확인을 기다림
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        // 재시도 횟수
        config.put(ProducerConfig.RETRIES_CONFIG, 10);
        // 메시지 전송 중복을 방지
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // default KafkaProducerFactory 를 생성해서 return 해준다.
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * kafka topic 에 데이터를 전송하기 위해 사용할 Kafka Template 을 생성
     */
    @Bean
    public KafkaTemplate<String, Long> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
