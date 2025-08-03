package com.cache.cachepractice.infrastructure.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cache.cachepractice.domain.movie.Movie;

public interface MovieJpaRepository extends JpaRepository<Movie, Long> {

	Optional<Movie> findByIdAndTitle(long id, String title);

	@Query("SELECT m FROM Movie m WHERE FUNCTION('MONTH', m.releaseDate) = :month")
	List<Movie> findByReleaseMonth(@Param("month") int month);

}
