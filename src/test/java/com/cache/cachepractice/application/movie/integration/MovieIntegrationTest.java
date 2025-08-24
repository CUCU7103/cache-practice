package com.cache.cachepractice.application.movie.integration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;
import org.springframework.transaction.annotation.Transactional;

import com.cache.cachepractice.application.movie.MovieInfo;
import com.cache.cachepractice.application.movie.MovieService;
import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.global.error.CustomErrorCode;
import com.cache.cachepractice.global.error.CustomException;
import com.cache.cachepractice.infrastructure.movie.MovieCacheRepository;
import com.cache.cachepractice.infrastructure.movie.MovieJpaRepository;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class MovieIntegrationTest {

	@Autowired
	private MovieJpaRepository movieJpaRepository;

	@Autowired
	private MovieCacheRepository movieCacheRepository;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MovieService movieService;


	@BeforeEach
	void setUp() {
		// DB, Redis 초기화
		movieJpaRepository.deleteAll();
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Test
	@DisplayName("아이디와 타이틀을 받아 영화 조회 성공")
	void successful_search_movie(){
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;

		Movie movie = movieJpaRepository.save(Movie.create(title, description, genre,date, duration));

		// act
		MovieInfo movieInfo = movieService.searchMovie(movie.getId(),movie.getTitle());

		//assert
		assertThat(movie.getId()).isEqualTo(movieInfo.id());
		assertThat(movie.getTitle()).isEqualTo(movieInfo.title());
		assertThat(movie.getDescription()).isEqualTo(movieInfo.description());

	}


	@Test
	@DisplayName("잘못된 아이디와 타이틀을 받아 영화 조회 실패")
	void failed_search_movie(){
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;

		Movie movie = movieJpaRepository.save(Movie.create(title, description, genre,date, duration));

		// act&assert
		assertThatThrownBy(() -> movieService.searchMovie(movie.getId(),"잘못된 제목형식"))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_MOVIE.getMessage());

	}


	@Test
	@DisplayName("특정 월의 영화 조회에 성공한다")
	void successful_search_movies_releaseMonth(){
		// arrange
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;

		Movie movie1 = Movie.create(title, description, genre,date, duration);
		Movie movie2 = Movie.create(title+1, description+1 , genre + 1, LocalDate.of(2025,8,10),duration	);
		Movie movie3 = Movie.create(title+2, description+2 , genre + 2, LocalDate.of(2025,8,27),duration );

		List<Movie> movieList = Arrays.asList(movie1,movie2,movie3);
		movieJpaRepository.saveAll(movieList);

		// act
		List<MovieInfo> results = movieService.searchMonthMovies(8);

		// assert
		assertThat(results)
			.hasSize(3)
			.extracting("releaseDate")
			.containsExactly(
				LocalDate.of(2025,8,15),
				LocalDate.of(2025,8,10),
				LocalDate.of(2025,8,27)
			);


	}

	@Test
	@DisplayName("캐시 MISS → DB 조회 → 캐시에 저장 후 반환")
	void integrationTest_cacheMissThenStore() {

		String CACHE_KEY_PREFIX = "movie-cache-key:";

		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;

		Movie sampleMovie = movieJpaRepository.save(Movie.create(title, description, genre,date, duration));
		log.info(sampleMovie.getId().toString());

		// 1) Redis에 값이 없어야 한다
		assertThat(redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + sampleMovie.getId()))
			.isNull();

		// 2) 서비스 호출 (캐시 MISS)
		MovieInfo info1 = movieService.searchMovieWithCache(
			sampleMovie.getId(), sampleMovie.getTitle());

		// 3) 반환값 검증
		assertThat(info1.id()).isEqualTo(sampleMovie.getId());
		assertThat(info1.title()).isEqualTo(sampleMovie.getTitle());

		// 4) 캐시에 저장됐는지 검증
		String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + sampleMovie.getId());
		assertThat(cachedJson).isNotNull();

		// 5) TTL이 설정되어 있다면 확인 (Optional)
		// Redis 기본 TTL 설정이 있다면, 아래처럼 확인할 수 있습니다.
		Long ttl = redisTemplate.getExpire(CACHE_KEY_PREFIX + sampleMovie.getId());
		// ttl이 0 보다 크면 존재하는 것이다.
		assertThat(ttl).isGreaterThan(0);
 }

 	@Test
 	@DisplayName("월별 영화 조회 캐시 기능 테스트 - 캐시 MISS → DB 조회 → 캐시 저장 → 캐시 HIT")
 	void integrationTest_monthMoviesCacheMissThenHit() {
 		// 캐시 키 접두사 정의
 		String CACHE_KEY_PREFIX = "movie-month-cache-key:";
 		int targetMonth = 8;
		
 		// 테스트 데이터 준비 - 8월 영화 3개 생성
 		LocalDate date = LocalDate.of(2025, 8, 15);
 		String title = "테스트 영화";
 		String description = "재밌는 영화";
 		String genre = "코미디";
 		Integer duration = 120;

 		Movie movie1 = Movie.create(title, description, genre, date, duration);
 		Movie movie2 = Movie.create(title + "2", description, genre, LocalDate.of(2025, 8, 10), duration);
 		Movie movie3 = Movie.create(title + "3", description, genre, LocalDate.of(2025, 8, 27), duration);

 		List<Movie> movieList = Arrays.asList(movie1, movie2, movie3);
 		movieJpaRepository.saveAll(movieList);

 		// 1) 초기 상태 확인 - Redis에 값이 없어야 함
 		assertThat(redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + targetMonth))
 			.isNull();

 		// 2) 첫 번째 서비스 호출 (캐시 MISS 예상)
 		List<MovieInfo> firstCallResults = movieService.searchMonthMoviesWithCache(targetMonth);

 		// 3) 반환값 검증 - 3개의 영화가 올바른 날짜로 반환되어야 함
 		assertThat(firstCallResults)
 			.hasSize(3)
 			.extracting("releaseDate")
 			.containsExactlyInAnyOrder(
 				LocalDate.of(2025, 8, 15),
 				LocalDate.of(2025, 8, 10),
 				LocalDate.of(2025, 8, 27)
 			);

 		// 4) 캐시에 저장됐는지 검증
 		String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + targetMonth);
 		assertThat(cachedJson).isNotNull();

 		// 5) TTL이 설정되어 있는지 확인
 		Long ttl = redisTemplate.getExpire(CACHE_KEY_PREFIX + targetMonth);
 		assertThat(ttl).isGreaterThan(0);

 		// 6) 두 번째 서비스 호출 (캐시 HIT 예상)
 		List<MovieInfo> secondCallResults = movieService.searchMonthMoviesWithCache(targetMonth);

 		// 7) 두 번째 호출의 결과도 동일한지 검증
 		assertThat(secondCallResults)
 			.hasSize(3)
 			.extracting("title")
 			.containsExactlyInAnyOrder(
 				title,
 				title + "2",
 				title + "3"
 			);
 	}

 }
