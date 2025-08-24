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

	@Cacheable(value = "movie", key = "#movieId", unless = "#result == null")
	@Transactional(readOnly = true)
	public MovieInfo searchMovieWithAnnotation(long movieId, String title) {
		log.info("Annotation-based Cache Miss - DB lookup for movie: {}", movieId);
		Movie movie = movieRepository.findMovieIdAndTitle(movieId, title)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MOVIE));
		return MovieInfo.from(movie);
	}

	@Cacheable(value = "monthlyMovies", key = "#month", unless = "#result == null or #result.isEmpty()")
	@Transactional(readOnly = true)
	public List<MovieInfo> searchMonthMoviesWithAnnotation(int month) {
		log.info("Annotation-based Cache Miss - DB lookup for month: {}", month);
		List<Movie> movies = movieRepository.findByReleaseMonth(month);
		return movies.stream().map(MovieInfo::from).toList();
	}
}
