package com.cache.cachepractice.infrastructure.movie;


import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.cache.cachepractice.application.movie.MovieInfo;
import com.cache.cachepractice.domain.movie.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MovieCacheRepositoryImpl implements MovieCacheRepository{

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private static final Duration CACHE_TTL = Duration.ofMinutes(10);

	private String key(Long id) {
		String CACHE_KEY_PREFIX = "movie-cache-key:";
		return CACHE_KEY_PREFIX + String.valueOf(id);
	}
	
	private String monthKey(int month) {
		String CACHE_KEY_PREFIX = "movie-month-cache-key:";
		return CACHE_KEY_PREFIX + month;
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
			redisTemplate.opsForValue().set(key(movie.getId()), serialized, CACHE_TTL);
			log.info("캐시 저장 성공");
		} catch (JsonProcessingException e) {
			log.error("Movie 직렬화 실패, 캐시 저장 실패. key={}", key, e);
		} catch (DataAccessException e) {
			log.error("Redis 접근 실패, 캐시 저장 실패. key={}", key, e);
		}
	}
	
	@Override
	public Optional<List<Movie>> getByMonth(int month) {
		String serialized = redisTemplate.opsForValue().get(monthKey(month));
		if (serialized == null) {
			// 값이 존재하지 않으면 빈 값 반환
			return Optional.empty();
		}
		log.info("month cache-hit for month: {}", month);
		try {
			// 역직렬화 - List<Movie> 타입으로 변환
			List<Movie> movies = objectMapper.readValue(serialized, new TypeReference<List<Movie>>() {});
			return Optional.of(movies);
		} catch (Exception e) {
			// 역직렬화 실패 시 캐시 비우고 빈 리턴
			log.error("월별 영화 역직렬화 실패, 캐시 삭제. month={}", month, e);
			redisTemplate.delete(monthKey(month));
			return Optional.empty();
		}
	}
	
	@Override
	public void putByMonth(int month, List<MovieInfo> movies) {
		String key = monthKey(month);
		try {

			// 직렬화 - List<MovieInfo>를 JSON 문자열로 변환
			String serialized = objectMapper.writeValueAsString(movies);
			log.info("직렬화 된 값 확인해보기 {}" , serialized);
			redisTemplate.opsForValue().set(key, serialized, CACHE_TTL);
			log.info("월별 영화 캐시 저장 성공. month={}, count={}", month, movies.size());
		} catch (JsonProcessingException e) {
			log.error("월별 영화 직렬화 실패, 캐시 저장 실패. month={}", month, e);
		} catch (DataAccessException e) {
			log.error("Redis 접근 실패, 월별 영화 캐시 저장 실패. month={}", month, e);
		}
	}
}
