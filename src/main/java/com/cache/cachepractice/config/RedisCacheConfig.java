package com.cache.cachepractice.config; // 패키지 선언

import org.springframework.cache.CacheManager; // 스프링 캐시 관리자 인터페이스
import org.springframework.context.annotation.Bean; // 스프링 빈(Bean)을 정의하기 위한 애노테이션
import org.springframework.context.annotation.Configuration; // 스프링 설정 클래스임을 나타내는 애노테이션
import org.springframework.data.redis.cache.RedisCacheConfiguration; // 레디스 캐시의 동작을 설정하기 위한 클래스
import org.springframework.data.redis.cache.RedisCacheManager; // 레디스 캐시 관리자 구현체
import org.springframework.data.redis.connection.RedisConnectionFactory; // 레디스 연결을 생성하는 팩토리 인터페이스
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer; // 객체를 JSON 형식으로 직렬화/역직렬화하는 클래스
import org.springframework.data.redis.serializer.RedisSerializationContext; // 직렬화 규칙을 정의하는 컨텍스트
import org.springframework.data.redis.serializer.StringRedisSerializer; // 문자열을 직렬화/역직렬화하는 클래스

import java.time.Duration; // 시간 단위를 표현하는 클래스
import java.util.HashMap; // 해시맵 자료구조
import java.util.Map; // 맵 인터페이스

@Configuration // 이 클래스가 스프링의 설정 정보를 담고 있음을 선언합니다.
public class RedisCacheConfig {

    @Bean // 이 메소드가 생성하는 객체를 스프링 컨테이너가 관리하는 빈(Bean)으로 등록합니다.
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) { // 캐시 매니저 빈을 생성하는 메소드입니다. 레디스 연결 팩토리를 주입받습니다.
        // 기본 캐시 설정을 정의합니다.
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // 캐시 키(Key)는 문자열 형식으로 직렬화합니다.
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // 캐시 값(Value)은 JSON 형식으로 직렬화하여, 객체 형태를 유지합니다.
                .entryTtl(Duration.ofMinutes(30)); // 캐시의 기본 유효 시간(Time-To-Live)을 30분으로 설정합니다.

        // 캐시의 이름별로 개별 설정을 담을 맵을 생성합니다.
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // "movie"라는 이름의 캐시에 대한 설정을 추가합니다. 유효 시간은 10분으로 설정합니다.
        cacheConfigurations.put("movie", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // "monthlyMovies"라는 이름의 캐시에 대한 설정을 추가합니다. 유효 시간은 1시간으로 설정합니다.
        cacheConfigurations.put("monthlyMovies", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 레디스 캐시 매니저를 빌더 패턴을 사용하여 생성합니다.
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig) // 모든 캐시에 적용될 기본 설정을 지정합니다.
                .withInitialCacheConfigurations(cacheConfigurations) // 캐시 이름별 개별 설정을 적용합니다.
                .build(); // 캐시 매니저 객체를 생성하여 반환합니다.
    }
}
