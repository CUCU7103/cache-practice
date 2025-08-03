package com.cache.cachepractice.presentation.movie.response;

import java.util.List;

import com.cache.cachepractice.application.movie.MovieInfo;

import lombok.Builder;

public record MovieListResponse(String message, List<MovieInfo> movieInfo) {
	@Builder
	public MovieListResponse(String message, List<MovieInfo> movieInfo) {
		this.message = message;
		this.movieInfo = movieInfo;
	}

	public static MovieListResponse of(String message,List<MovieInfo> movieInfo){
		return new MovieListResponse( message, movieInfo);
	}

}
