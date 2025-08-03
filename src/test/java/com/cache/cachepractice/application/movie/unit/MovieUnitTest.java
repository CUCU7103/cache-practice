package com.cache.cachepractice.application.movie.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;


import java.time.LocalDate;
import java.util.Optional;

import com.cache.cachepractice.application.movie.MovieInfo;
import com.cache.cachepractice.application.movie.MovieService;
import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;
import com.cache.cachepractice.global.error.CustomErrorCode;
import com.cache.cachepractice.global.error.CustomException;
import com.cache.cachepractice.infrastructure.movie.MovieCacheRepository;
import com.cache.cachepractice.infrastructure.movie.MovieJpaRepository;

@ExtendWith(MockitoExtension.class)
public class MovieUnitTest {
	@Mock
	private MovieRepository movieRepository;

	@Mock
	private MovieJpaRepository movieJpaRepository;

	@Mock
	private MovieCacheRepository movieCacheRepository;

	@InjectMocks
	private MovieService movieService;

	private Movie sampleMovie;

	private final long MOVIE_ID = 42L;
	private final String TITLE = "테스트 영화";


	@BeforeEach
	void setUp() {
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;

		sampleMovie = Movie.create(title, description, genre,date, duration);
	}

	@Test
	@DisplayName("존재하지 않는 영화 검색시 예외를 발생시킨다")
	void search_failed_not_exist_movie(){
		// arrange
		long movieId  = 2030042;
		String title ="존재하지 않는 영화";

		// act
		given(movieRepository.findMovieIdAndTitle(movieId, title))
			.willThrow(new CustomException(CustomErrorCode.NOT_FOUND_MOVIE));

		// assertThat
		assertThatThrownBy(() -> movieService.searchMovie(movieId,title)).isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_MOVIE.getMessage());

	}


	@Test
	@DisplayName("캐시 히트 시, DB 호출 없이 캐시된 값을 반환해야 한다")
	void cacheHitReturnsCachedValue() {
		// given
		given(movieCacheRepository.get(MOVIE_ID))
			.willReturn(Optional.of(sampleMovie));

		// when
		MovieInfo result = movieService.searchMovieWithCache(MOVIE_ID, TITLE);

		// then
		assertThat(result.title()).isEqualTo(TITLE);

		// movieRepository는 전혀 호출되지 않아야 한다
		then(movieRepository).should(never()).findMovieIdAndTitle(anyLong(), anyString());
	}

	@Test
	@DisplayName("캐시 미스 시, DB 조회 후 캐시에 저장하고 값을 반환해야 한다")
	void cacheMissLoadsFromDbAndCaches() {
		// arrange
		given(movieCacheRepository.get(MOVIE_ID))
			.willReturn(Optional.empty());
		given(movieRepository.findMovieIdAndTitle(MOVIE_ID, TITLE))
			.willReturn(Optional.of(sampleMovie));

		// act
		MovieInfo result = movieService.searchMovieWithCache(MOVIE_ID, TITLE);

		// assert
		assertThat(result.title()).isEqualTo(TITLE);

		// 캐시에 저장됐는지 확인
		then(movieCacheRepository).should().put(sampleMovie);
	}

	@Test
	@DisplayName("DB에도 없으면 CustomException을 던진다")
	void missingInCacheAndDbThrows() {
		// given
		given(movieCacheRepository.get(MOVIE_ID)).willReturn(Optional.empty());
		given(movieRepository.findMovieIdAndTitle(MOVIE_ID, TITLE))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> movieService.searchMovieWithCache(MOVIE_ID, TITLE))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_MOVIE.getMessage());

		// 캐시에 저장은 절대 호출되지 않아야 한다
		then(movieCacheRepository).should(never()).put(any());
	}





}
