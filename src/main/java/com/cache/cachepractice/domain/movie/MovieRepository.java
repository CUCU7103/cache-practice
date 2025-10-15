package com.cache.cachepractice.domain.movie;

import com.cache.cachepractice.domain.movie.schedule.MovieSchedule;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {

	Optional<Movie> findMovieIdAndTitle(long movieId, String title);

    Optional <Movie> findMovieId(long movieId);

	Movie save(Movie movie);

	List<Movie> findByReleaseMonth(int Month);

	List<MovieSchedule> findAllMovieScheduleByMovieId(long movieId);
}
