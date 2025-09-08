package com.cache.cachepractice.infrastructure.movie.jpa;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import com.cache.cachepractice.infrastructure.movie.MovieRepositoryImpl;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.MovieRepository;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MovieRepositoryImpl.class)
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
