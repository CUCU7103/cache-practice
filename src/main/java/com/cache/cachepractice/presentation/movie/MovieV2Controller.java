package com.cache.cachepractice.presentation.movie;

import com.cache.cachepractice.application.movie.MovieService;
import com.cache.cachepractice.presentation.movie.response.MovieListResponse;
import com.cache.cachepractice.presentation.movie.response.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/movies")
@RequiredArgsConstructor
public class MovieV2Controller {

    private final MovieService movieService;

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieResponse> searchMovie(
            @PathVariable(name = "movieId") long movieId,
            @RequestParam(name = "title") String title) {
        return ResponseEntity.ok().body(MovieResponse.of("[@Cacheable] 조회 성공", movieService.searchMovieWithAnnotation(movieId, title)));
    }

    @GetMapping("/monthly/{month}")
    public ResponseEntity<MovieListResponse> searchMovieReleaseMonth(
            @PathVariable(name = "month") int month) {
        return ResponseEntity.ok().body(MovieListResponse.of("[@Cacheable] 월별 조회 성공", movieService.searchMonthMoviesWithAnnotation(month)));
    }
}
