package com.cache.cachepractice.domain.movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

	Optional<Movie> findMovieIdAndTitle(long id, String title);

	Movie save(Movie movie);

	List<Movie> findByReleaseMonth(int Month);

	
}
