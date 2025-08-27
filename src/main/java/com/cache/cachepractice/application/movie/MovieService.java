package com.cache.cachepractice.application.movie;

import static org.springframework.data.jpa.domain.AbstractPersistable_.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;
import com.cache.cachepractice.global.error.CustomErrorCode;
import com.cache.cachepractice.global.error.CustomException;
import com.cache.cachepractice.infrastructure.movie.MovieCacheRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

	private final MovieRepository movieRepository;
	private final MovieCacheRepository movieCacheRepository;

	// 영화 단건 조회 (pk를 인덱스로 사용함)
	@Transactional(readOnly = true)
	public MovieInfo searchMovie(long movieId, String title){

		Movie movie = movieRepository.findMovieIdAndTitle(movieId,title)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MOVIE));

		return MovieInfo.from(movie);
	}

	// 특정 월의 개봉영화를 조회하기
	@Transactional(readOnly = true)
	public List<MovieInfo> searchMonthMovies(int month){
		List<Movie> movies = movieRepository.findByReleaseMonth(month);
		return movies.stream().map(MovieInfo::from).toList();
	}

	// 특정 월의 개봉영화를 Redis 캐시를 활용하여 조회하기
	@Transactional(readOnly = true)
	public List<MovieInfo> searchMonthMoviesWithCache(int month){
		// 1) 캐시 확인
		Optional<List<Movie>> cachedMovies = movieCacheRepository.getByMonth(month);
		if(cachedMovies.isPresent()){
			log.info("월별 영화 캐시 Hit. month={}", month);
			return cachedMovies.get().stream().map(MovieInfo::from).toList();
		}

		// 2) 캐시 miss시 DB 조회
		log.info("월별 영화 캐시 miss. month={}", month);
		List<Movie> movies = movieRepository.findByReleaseMonth(month);
		
		// 3) 캐시 저장
		if(!movies.isEmpty()) {
			log.info("월별 영화 캐시 저장. month={}, count={}", month, movies.size());
			// 순환참조 문제 해결을 위한 변환
			List<MovieInfo> dtoList = movies.stream()
				.map(MovieInfo::from)
				.toList();
			movieCacheRepository.putByMonth(month, dtoList);
		}

		return movies.stream().map(MovieInfo::from).toList();
	}


	// 캐싱 적용
	// cache-aside 방식
	@Transactional(readOnly = true)
	public MovieInfo searchMovieWithCache(long movieId, String title){
		// 1) 캐시 확인
		if(movieCacheRepository.get(movieId).isPresent()){
			log.info("캐시 Hit");
			return MovieInfo.from(movieCacheRepository.get(movieId).get());
		}

		log.info("캐시 miss");
		Movie movie = movieRepository.findMovieIdAndTitle(movieId,title)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MOVIE));

		// 캐시 저장
		log.info("캐시 miss시 캐시 저장");
		movieCacheRepository.put(movie);

		return MovieInfo.from(movie);
	}


	// --- Below is the code with @Cacheable annotation for comparison ---

	// @Cacheable 애노테이션을 사용한 단일 영화 조회 메소드입니다.
	@Cacheable(value = "movie", key = "#movieId", unless = "#result == null") // "movie" 캐시를 사용하고, 메소드의 파라미터인 movieId를 키로 사용합니다. 단, 결과가 null일 경우는 캐시하지 않습니다.
	@Transactional(readOnly = true) // 이 메소드는 데이터베이스를 읽기만 하는 트랜잭션임을 명시합니다.
	public MovieInfo searchMovieWithAnnotation(long movieId, String title) { // 애노테이션 기반 캐싱을 사용하는 영화 조회 메소드입니다.
		log.info("Annotation-based Cache Miss - DB lookup for movie: {}", movieId); // 캐시 미스(Cache Miss)가 발생하여 DB를 조회할 경우 로그를 남깁니다.
		Movie movie = movieRepository.findMovieIdAndTitle(movieId, title) // 데이터베이스에서 영화 ID와 제목으로 영화를 찾습니다.
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MOVIE)); // 영화를 찾지 못하면 예외를 발생시킵니다.
		return MovieInfo.from(movie); // 조회된 영화 정보를 MovieInfo 객체로 변환하여 반환합니다.
	}

	// @Cacheable 애노테이션을 사용한 월별 영화 목록 조회 메소드입니다.
	@Cacheable(value = "monthlyMovies", key = "#month", unless = "#result == null or #result.isEmpty()") // "monthlyMovies" 캐시를 사용하고, 파라미터인 month를 키로 사용합니다. 단, 결과가 null이거나 비어있을 경우는 캐시하지 않습니다.
	@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 설정합니다.
	public List<MovieInfo> searchMonthMoviesWithAnnotation(int month) { // 애노테이션 기반 캐싱을 사용하는 월별 영화 조회 메소드입니다.
		log.info("Annotation-based Cache Miss - DB lookup for month: {}", month); // 캐시 미스가 발생하여 DB를 조회할 경우 로그를 남깁니다.
		List<Movie> movies = movieRepository.findByReleaseMonth(month); // 데이터베이스에서 해당 월에 개봉한 영화 목록을 조회합니다.
		return movies.stream().map(MovieInfo::from).toList(); // 조회된 영화 목록을 MovieInfo 객체 리스트로 변환하여 반환합니다.
	}
}
