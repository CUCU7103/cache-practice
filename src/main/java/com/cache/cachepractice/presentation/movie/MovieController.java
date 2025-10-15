package com.cache.cachepractice.presentation.movie;

import com.cache.cachepractice.presentation.movie.response.MovieScheduleListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cache.cachepractice.application.movie.MovieService;
import com.cache.cachepractice.presentation.movie.response.MovieListResponse;
import com.cache.cachepractice.presentation.movie.response.MovieResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

	private final MovieService movieService;

	@GetMapping("/{movieId}")
	public ResponseEntity<MovieResponse> searchMovie(@PathVariable(name = "movieId") long movieId, @RequestParam(name= "title") String title) {
		return ResponseEntity.ok()
            .body(MovieResponse.of("조회 성공",movieService.searchMovie(movieId, title)));
	}

	@GetMapping("/month/{month}")
	public ResponseEntity<MovieListResponse> searchMovieReleaseMonth(@PathVariable(name = "month") int month){
		return ResponseEntity.ok()
            .body(MovieListResponse.of("조회 성공", movieService.searchMonthMovies(month)));
	}

    @GetMapping("{movieId}/movieSchedule")
    public ResponseEntity<MovieScheduleListResponse> searchMovieSchedules(@PathVariable(name = "movieId") long movieId){
        return ResponseEntity.ok()
            .body(MovieScheduleListResponse.of("조회 성공", movieService.searchSchedules(movieId)));
    }



}
