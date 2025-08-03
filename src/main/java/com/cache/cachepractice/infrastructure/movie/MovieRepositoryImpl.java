package com.cache.cachepractice.infrastructure.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;

import lombok.RequiredArgsConstructor;

// 구현체에 Repository 어노테이션을 붙이는 이유는

/**
 * 현재 프로젝트 구조에서 MovieRepositoryImpl은 MovieRepository라는 인터페이스의 구현체야.
 *
 * 실제로 JPA를 통해 DB와 통신하는 로직이 MovieRepositoryImpl에 있으니,
 * 이 클래스를 스프링이 빈으로 인식해야 다른 곳에서 의존성 주입(@Autowired, 생성자 주입 등)이 가능해.
 *
 * 즉, @Repository를 붙이면,
 *
 * 스프링이 이 구현체를 빈으로 등록
 *
 * 다른 컴포넌트에서 사용할 수 있게 됨
 *
 * 데이터 접근 계층 예외를 Spring의 DataAccessException으로 변환
 */
@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepository {

	private final MovieJpaRepository movieJpaRepository;

	@Override
	public Optional<Movie> findMovieIdAndTitle(long id ,String title) {
		return movieJpaRepository.findByIdAndTitle(id,title);
	}

	@Override
	public Movie save(Movie movie) {
		return movieJpaRepository.save(movie);
	}

	@Override
	public List<Movie> findByReleaseMonth(int month) {
		return movieJpaRepository.findByReleaseMonth(month);
	}
}
