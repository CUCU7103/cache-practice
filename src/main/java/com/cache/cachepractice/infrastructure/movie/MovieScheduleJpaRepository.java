package com.cache.cachepractice.infrastructure.movie;

import com.cache.cachepractice.domain.movie.schedule.MovieSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieScheduleJpaRepository extends JpaRepository<MovieSchedule, Long> {
    List<MovieSchedule> findAllByMovieId(long movieId);
}
