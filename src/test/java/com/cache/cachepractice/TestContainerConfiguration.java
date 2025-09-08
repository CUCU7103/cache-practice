package com.cache.cachepractice;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

@Configuration
public class TestContainerConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("cache_test")
			.withUsername("test")
			.withPassword("test")
            // ✅ 빠른 기동 플래그
            .withCommand(
                "--skip-log-bin",                      // 바이너리 로그 비활성
                "--skip-name-resolve",                 // DNS 역조회 비활성
                "--performance-schema=OFF",            // Performance Schema 비활성
                "--innodb_flush_log_at_trx_commit=2",  // fsync 강도 완화(테스트용)
                "--sync-binlog=0",                     // binlog fsync 완화(binlog off 시 영향 없음)
                "--skip-ssl"                           // SSL 비활성
            );
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url",
			MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
	}


	// --- Redis 컨테이너 (추가) ---
	public static final GenericContainer<?> REDIS_CONTAINER;

	static {
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2.1-alpine"))
			.withExposedPorts(6379)
			.withCreateContainerCmdModifier(cmd ->
				cmd.withHostConfig(
					new HostConfig()
						.withPortBindings(
							new PortBinding(
								Ports.Binding.bindPort(6380),
								new ExposedPort(6379)
							)
						)
				)
			);
		REDIS_CONTAINER.start();

		// Spring Data Redis (Lettuce/StringRedisTemplate)
		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());

		// Also set legacy properties for libraries expecting spring.redis.*
		System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());

		// Redisson Spring Boot Starter single-server 설정
		System.setProperty(
			"spring.redisson.single-server-config.address",
			String.format("redis://%s:%d",
				REDIS_CONTAINER.getHost(),
				REDIS_CONTAINER.getFirstMappedPort()
			)
		);
	}

}
