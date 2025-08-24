package com.cache.cachepractice.infrastructure.movie;

import java.util.List;
import java.util.Optional;

import com.cache.cachepractice.application.movie.MovieInfo;
import com.cache.cachepractice.domain.movie.Movie;

public interface MovieCacheRepository {

	Optional<Movie> get(Long id);

	void put(Movie movie);
	
	Optional<List<Movie>> getByMonth(int month);
	
	void putByMonth(int month, List<MovieInfo> movies);

}
