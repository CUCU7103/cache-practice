package com.cache.cachepractice.domain.movie;

import static jakarta.persistence.ConstraintMode.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cache.cachepractice.domain.movie.schedule.MovieSchedule;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Table(name = "movie")
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;               // 영화 고유 ID

	@Column(name = "title", nullable = false, length = 100)
	private String title;          // 영화 제목

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;    // 영화 설명

	@Column(name = "genre", length = 50)
	private String genre;          // 장르

	@Column(name = "release_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate releaseDate; // 개봉일

	@Column(name = "duration")
	private Integer duration;      // 상영 시간(분)

	//  mappedBy = "movie",(inverse) 비소유,
	// 매핑 소유권은 보통 ManyToOne에 존재한다.
	@OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MovieSchedule> movieSchedules = new ArrayList<>();

	@Builder
	private Movie(String title, String description, String genre, LocalDate releaseDate, Integer duration) {
		this.title = title;
		this.description = description;
		this.genre = genre;
		this.releaseDate = releaseDate;
		this.duration = duration;
	}

	public static Movie create(String title, String description, String genre, LocalDate releaseDate, Integer duration) {
		return Movie.builder()
			.title(title)
			.description(description)
			.genre(genre)
			.releaseDate(releaseDate)
			.duration(duration)
			.build();
	}

}
