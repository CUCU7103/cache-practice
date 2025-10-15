package com.cache.cachepractice.presentation.movie.response;

import com.cache.cachepractice.application.movie.MovieInfo;
import com.cache.cachepractice.application.movie.MovieScheduleInfo;
import java.util.List;
import lombok.Builder;

public record MovieScheduleListResponse(String message, List<MovieScheduleInfo> movieScheduleInfoList) {
	@Builder
	public MovieScheduleListResponse(String message, List<MovieScheduleInfo> movieScheduleInfoList) {
		this.message = message;
		this.movieScheduleInfoList = movieScheduleInfoList;
	}

	public static MovieScheduleListResponse of(String message,List<MovieScheduleInfo> movieScheduleInfoList){
		return new MovieScheduleListResponse( message, movieScheduleInfoList);
	}

}
