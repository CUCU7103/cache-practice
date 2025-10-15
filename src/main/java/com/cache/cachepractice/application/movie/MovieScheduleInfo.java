package com.cache.cachepractice.application.movie;

import com.cache.cachepractice.domain.movie.schedule.MovieSchedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;

public record MovieScheduleInfo(long id , LocalDate date, LocalTime time, String theater , long movieId ) {

    @Builder
    public MovieScheduleInfo(long id, LocalDate date, LocalTime time, String theater,
        long movieId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.theater = theater;
        this.movieId = movieId;
    }

    public static MovieScheduleInfo from (MovieSchedule movieSchedule) {
        return MovieScheduleInfo.builder()
            .id(movieSchedule.getId())
            .date(movieSchedule.getDate())
            .time(movieSchedule.getTime())
            .theater(movieSchedule.getTheater())
            .movieId(movieSchedule.getMovie().getId())
            .build();
    }
}
