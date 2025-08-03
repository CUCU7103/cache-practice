package com.cache.cachepractice.infrastructure.movie.redis;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;
import com.cache.cachepractice.infrastructure.movie.MovieCacheRepositoryImpl;
import com.cache.cachepractice.infrastructure.movie.MovieJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
public class MovieCacheRepositoryIntegrationTest {

	@Autowired
	private MovieCacheRepositoryImpl cacheRepo;

	@Autowired
	private MovieJpaRepository movieJpaRepository;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void beforeEach() {
		// 혹시 남아있는 캐시가 있으면 클리어
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Test
	@DisplayName("put() 호출 시 Redis에 JSON 직렬화되어 저장되어야 한다")
	void testPutStoresJsonInRedis() throws Exception {
		// given
		Movie movie = movieJpaRepository.save( Movie.create(
			"제목", "설명", "장르",
			LocalDate.of(2025, 8, 2), 120
		));
		// when
		cacheRepo.put(movie);  // 내부에서 TTL 10분 설정하여 저장

		String CACHE_KEY = "movie-cache-key:" + movie.getId();

		// then
		String storedJson = redisTemplate.opsForValue().get(CACHE_KEY);
		assertThat(storedJson).isNotNull()
			.as("Redis에 JSON 문자열이 저장되어 있어야 한다");

		Movie fromCache = objectMapper.readValue(storedJson, Movie.class);
		assertThat(fromCache.getTitle()).isEqualTo(movie.getTitle());
		assertThat(fromCache.getReleaseDate()).isEqualTo(movie.getReleaseDate());
	}

	@Test
	@DisplayName("put() 호출 시 TTL(만료시간)이 설정되어 있어야 한다")
	void testPutSetsTtl() {
		// given
		Movie movie = Movie.create(
			"다른제목", "다른설명", "코미디",
			LocalDate.of(2025, 7, 30), 90
		);

		// when
		cacheRepo.put(movie);

		// then
		Long ttl = redisTemplate.getExpire("movie-cache-key:" + movie.getId());
		assertThat(ttl).isNotNull()
			.isGreaterThan(0)
			.as("TTL이 0보다 커야 한다");
	}

	@Test
	@DisplayName("역직렬화 에러 발생 시 캐시 키가 삭제되어야 한다")
	void testPutWithBadObjectDoesNotLeaveCorruptCache() throws Exception {

		Movie movie = movieJpaRepository.save( Movie.create(
			"제목", "설명", "장르",
			LocalDate.of(2025, 8, 2), 120
		));

		String CACHE_KEY = "movie-cache-key:" + movie.getId();

		// given: 캐시에 잘못된 JSON 강제로 삽입
		redisTemplate.opsForValue().set(CACHE_KEY, "###BAD_JSON###");

		// when
		// get() 을 호출하면 내부에서 delete(key) 처리됨
		var maybe = cacheRepo.get(movie.getId());

		// then
		assertThat(maybe).isEmpty();
		assertThat(redisTemplate.hasKey(CACHE_KEY)).isFalse()
			.as("역직렬화 실패 시 캐시가 삭제되어야 한다");
	}

}
