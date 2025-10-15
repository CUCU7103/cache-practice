package com.cache.cachepractice.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
		log.error("Unhandled Exception occurred at [{} {}]", request.getMethod(), request.getRequestURI(), e);
		return ResponseEntity.status(500).body(new ErrorResponse("500", e.getMessage()));
	}

	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<ErrorResponse> handleException(CustomException e, HttpServletRequest request) {
		log.warn("Handled CustomException at [{} {}] - code: {}, message: {}, location: {}:{}",
			request.getMethod(), request.getRequestURI(),
			e.getCustomErrorCode().getCode(), e.getMessage(),
			e.getStackTrace()[0].getFileName(),  // 파일명
			e.getStackTrace()[0].getLineNumber() // 라인 번호
		);
		return ResponseEntity.status(e.getCustomErrorCode().getHttpStatus())
			.body(new ErrorResponse(e.getCustomErrorCode().getCode(), e.getMessage()));

	}

}
