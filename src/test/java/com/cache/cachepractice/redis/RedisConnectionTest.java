package com.cache.cachepractice.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
public class RedisConnectionTest {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	@DisplayName("Redis 연결 검증 테스트 진행")
	void successful_connection(){
		// act
		redisTemplate.opsForValue().set("Test","Success");

		// act
		assertThat(redisTemplate.hasKey("Test")).isTrue();

	}


}
