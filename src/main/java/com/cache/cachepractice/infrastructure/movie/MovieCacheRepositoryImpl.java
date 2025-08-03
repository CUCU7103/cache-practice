package com.cache.cachepractice.infrastructure.movie;


import java.time.Duration;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.cache.cachepractice.domain.movie.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MovieCacheRepositoryImpl implements MovieCacheRepository{

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private String key(long id) {
		String CACHE_KEY_PREFIX = "movie-cache-key:";
		return CACHE_KEY_PREFIX + id;
	}

	@Override
	public Optional<Movie> get(Long id) {
		String serialized = redisTemplate.opsForValue().get(key(id));
		if (serialized == null) {
			// 값이 존재하지 않으면 빈 값 반환
			return Optional.empty();
		}
		log.info("cache-hit");
		try {
			// 역직렬화
			Movie m = objectMapper.readValue(serialized, Movie.class);
			return Optional.of(m);
		} catch (Exception e) {
			// 역직렬화 실패 시 캐시 비우고 빈 리턴
			redisTemplate.delete(key(id));
			return Optional.empty();
		}

	}

	@Override
	public void put(Movie movie) {
		String key = key(movie.getId());
		try {
			// 직렬화
			String serialized = objectMapper.writeValueAsString(movie);
			redisTemplate.opsForValue().set(key(movie.getId()), serialized , Duration.ofMinutes(10));
			log.info("캐시 저장 성공");
		} catch (JsonProcessingException e) {
			log.error("Movie 직렬화 실패, 캐시 저장 실패. key={}", key, e);
		} catch (DataAccessException e) {
			log.error("Redis 접근 실패, 캐시 저장 실패. key={}", key, e);
		}
	}
}
