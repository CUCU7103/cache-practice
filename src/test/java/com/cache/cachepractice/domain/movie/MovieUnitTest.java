package com.cache.cachepractice.domain.movie;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class MovieUnitTest {

	@Test
	@DisplayName("Movie 객체 생성 테스트")
	void create_movie(){
		// arrange
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;
		// act
		Movie movie = Movie.create(title, description, genre,date, duration);
		// asssert
		assertThat(movie.getTitle()).isEqualTo(title);
		assertThat(movie.getDescription()).isEqualTo(description);
	}
}
