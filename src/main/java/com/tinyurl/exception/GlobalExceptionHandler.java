package com.tinyurl.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ShortUrlNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleShortUrlNotFound(ShortUrlNotFoundException exception) {

		ErrorResponse response = new ErrorResponse(exception.getMessage(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(InvalidUrlException.class)
	public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException exception) {

		ErrorResponse response = new ErrorResponse(exception.getMessage(), LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {

		String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

		ErrorResponse response = new ErrorResponse(message, LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}