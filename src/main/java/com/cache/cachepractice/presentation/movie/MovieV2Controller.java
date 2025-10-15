package com.cache.cachepractice.presentation.movie; // 패키지 선언

import com.cache.cachepractice.application.movie.MovieService; // 비즈니스 로직을 처리하는 MovieService 클래스
import com.cache.cachepractice.presentation.movie.response.MovieListResponse; // 월별 영화 목록 응답을 위한 클래스
import com.cache.cachepractice.presentation.movie.response.MovieResponse; // 단일 영화 응답을 위한 클래스
import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 자동으로 생성해주는 Lombok 애노테이션
import org.springframework.http.ResponseEntity; // HTTP 응답을 표현하는 클래스
import org.springframework.web.bind.annotation.*; // Spring Web MVC 애노테이션 (RestController, RequestMapping 등)

@RestController // 이 클래스가 RESTful 웹 서비스의 컨트롤러임을 나타냅니다.
@RequestMapping("/api/v2/movies") // 이 컨트롤러의 모든 핸들러 메소드에 대한 기본 URL 경로를 "/api/v2/movies"로 지정합니다.
@RequiredArgsConstructor // final로 선언된 movieService 필드에 대한 생성자를 자동으로 생성합니다.
public class MovieV2Controller {

    private final MovieService movieService; // 비즈니스 로직을 수행할 MovieService 객체입니다.

   /* @GetMapping("/{movieId}") // HTTP GET 요청을 "/{movieId}" 경로와 매핑합니다.
    public ResponseEntity<MovieResponse> searchMovie( // 단일 영화를 조회하는 API 엔드포인트입니다.
            @PathVariable(name = "movieId") long movieId, // URL 경로에서 movieId 값을 추출하여 메소드 파라미터로 받습니다.
            @RequestParam(name = "title") String title) { // 요청 파라미터에서 title 값을 추출하여 메소드 파라미터로 받습니다.
        // movieService의 애노테이션 기반 캐싱 메소드를 호출하고, 그 결과를 성공(200 OK) 응답으로 반환합니다.
        return ResponseEntity.ok().body(MovieResponse.of("[@Cacheable] 조회 성공", movieService.searchMovieWithAnnotation(movieId, title)));
    }

    @GetMapping("/monthly/{month}") // HTTP GET 요청을 "/monthly/{month}" 경로와 매핑합니다.
    public ResponseEntity<MovieListResponse> searchMovieReleaseMonth( // 월별 개봉 영화를 조회하는 API 엔드포인트입니다.
            @PathVariable(name = "month") int month) { // URL 경로에서 month 값을 추출하여 메소드 파라미터로 받습니다.
        // movieService의 애노테이션 기반 월별 조회 메소드를 호출하고, 그 결과를 성공(200 OK) 응답으로 반환합니다.
        return ResponseEntity.ok().body(MovieListResponse.of("[@Cacheable] 월별 조회 성공", movieService.searchMonthMoviesWithAnnotation(month)));
    }*/
}
