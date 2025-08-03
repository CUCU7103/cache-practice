package com.cache.cachepractice.domain.schedule;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cache.cachepractice.domain.movie.Movie;
import com.cache.cachepractice.domain.movie.schedule.MovieSchedule;

public class MovieScheduleUnitTest {



	@Test
	@DisplayName("영화 스케줄 생성 테스트 ")
	void create_movieSchedule() {
		LocalDate date = LocalDate.of(2025,8, 15);
		String title= "테스트 영화";
		String description = "재밌는 영화";
		String genre = "코미디";
		Integer duration = 120;

		Movie movie = Movie.create(title, description, genre,date, duration);

		LocalDate mdate = LocalDate.of(2025,9,15);
		LocalTime time = LocalTime.of(14,20);
		String theater = "수원 스타필드";
		//act
		MovieSchedule movieSchedule = MovieSchedule.create(movie,mdate,time,theater);
		// assert
		assertThat(movieSchedule.getDate()).isEqualTo(mdate);
		assertThat(movie).isEqualTo(movieSchedule.getMovie());

	}

}
