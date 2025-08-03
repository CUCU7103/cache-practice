package com.cache.cachepractice.presentation.movie.response;

import com.cache.cachepractice.application.movie.MovieInfo;

import lombok.Builder;

public record MovieResponse(String message, MovieInfo movieInfo) {
	@Builder
	public MovieResponse(String message, MovieInfo movieInfo) {
		this.message = message;
		this.movieInfo = movieInfo;
	}

	public static MovieResponse of(String message,MovieInfo movieInfo){
		return new MovieResponse( message, movieInfo);
	}

}
