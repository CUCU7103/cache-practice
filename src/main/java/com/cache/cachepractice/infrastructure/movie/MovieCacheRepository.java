package com.cache.cachepractice.infrastructure.movie;

import java.util.Optional;

import com.cache.cachepractice.domain.movie.Movie;

public interface MovieCacheRepository {

	Optional<Movie> get(Long id);

	void put(Movie movie);

}
