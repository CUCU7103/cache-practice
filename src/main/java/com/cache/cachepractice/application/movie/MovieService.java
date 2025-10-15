package com.cache.cachepractice.application.movie;

import com.cache.cachepractice.domain.movie.schedule.MovieSchedule;
import com.cache.cachepractice.infrastructure.movie.MovieCacheRepository;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;
import com.cache.cachepractice.global.error.CustomErrorCode;
import com.cache.cachepractice.global.error.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @Transactional(readOnly = true)
    public List<MovieScheduleInfo> searchSchedules(long movieId) {
        List<MovieSchedule> movieSchedule = movieRepository.findAllMovieScheduleByMovieId(movieId);
        return movieSchedule.stream().map(MovieScheduleInfo::from).toList();

    }

	// 특정 월의 개봉영화를 조회하기
	@Transactional(readOnly = true)
	public List<MovieInfo> searchMonthMovies(int month){
		List<Movie> movies = movieRepository.findByReleaseMonth(month);
		return movies.stream().map(MovieInfo::from).toList();
	}

    

    @Transactional
    public MovieInfo searchMovieForCache(long movieId){
        // cache - aside
        // 1. 먼저 캐시 조회 하고 없으면 db 조회 후 값을 가져옴
        Optional<Movie> cachedMovie = movieCacheRepository.get(movieId);
        if(cachedMovie.isPresent()){
            return MovieInfo.from(cachedMovie.get());
        }
        // 캐시에 값이 없다면 DB 조회
        Movie movie = movieRepository.findMovieId(movieId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MOVIE));
        // 그리고 캐시에 저장
        movieCacheRepository.put(movie);
        // 값 반환하기
        return MovieInfo.from(movie);
    }
    
    

/*
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
	}*/
}
