package com.cache.cachepractice.application.movie;

import java.time.LocalDate;

import com.cache.cachepractice.domain.movie.Movie;

import lombok.Builder;

public record MovieInfo(Long id, String title, String description, String genre, LocalDate releaseDate, Integer duration)  {

	@Builder
	public MovieInfo(Long id ,String title, String description, String genre, LocalDate releaseDate, Integer duration) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.genre = genre;
		this.releaseDate = releaseDate;
		this.duration = duration;
	}

	public static MovieInfo from(Movie movie) {
		return MovieInfo.builder()
			.id(movie.getId())
			.title(movie.getTitle())
			.description(movie.getDescription())
			.genre(movie.getGenre())
			.releaseDate(movie.getReleaseDate())
			.duration(movie.getDuration())
			.build();
	}
}
