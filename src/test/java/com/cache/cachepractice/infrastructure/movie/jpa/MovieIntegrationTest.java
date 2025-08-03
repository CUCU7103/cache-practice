package com.cache.cachepractice.infrastructure.movie.jpa;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;

@Transactional
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class MovieIntegrationTest {

	@Autowired
	private MovieRepository movieRepository;

	@Test
	@DisplayName("Movie 조회 테스트")
	void search_movie(){
		// arrange
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;
		// act
		Movie movie = Movie.create(title, description, genre,date, duration);
		Movie creatMovie = movieRepository.save(movie);
		//
		assertThat(movie.getTitle()).isEqualTo(creatMovie.getTitle());

	}





}
