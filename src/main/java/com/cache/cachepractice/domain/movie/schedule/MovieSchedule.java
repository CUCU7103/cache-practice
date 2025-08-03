package com.cache.cachepractice.domain.movie.schedule;

import static jakarta.persistence.ConstraintMode.*;

import java.time.LocalDate;
import java.time.LocalTime;

import com.cache.cachepractice.domain.movie.Movie;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name ="movie_schedules")
public class MovieSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;         // 스케줄 고유 ID

	@Column(name = "date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;  // 상영 날짜

	@Column(name = "time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
	private LocalTime time;     // 상영 시간 (예: "14:30")

	@Column(name = "theater")
	private String theater;  // 상영관 정보

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// NO_CONSTRAINT를 지정하면 물리적인(foreign key) 제약조건을 생성하지 말라
	// NO_CONSTRAINT 지정 시
	// 외래키 컬럼은 생기지만, DB 레벨의 FOREIGN KEY 제약조건은 아예 생성되지 않습니다.
	// 즉, 데이터 무결성 검증(참조 무결성)을 애플리케이션 레이어나 다른 방식으로 관리해야 해요.
	@JoinColumn(name = "movie_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Movie movie;  // FK 소유자


	@Builder
	private MovieSchedule(Movie movie, LocalDate date, LocalTime time, String theater) {
		this.movie = movie;
		this.date = date;
		this.time = time;
		this.theater = theater;
	}


	public static MovieSchedule create(Movie movie, LocalDate date, LocalTime time, String theater) {
		return MovieSchedule.builder()
			.movie(movie)
			.date(date)
			.time(time)
			.theater(theater)
			.build();
	}



}
