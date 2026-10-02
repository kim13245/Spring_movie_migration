package com.example.movieproject.common.exception;

import com.example.movieproject.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();
    return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiResponse.error(errorCode.getMessage()));
  }

  // TMDB에 없는 영화를 조회한 경우 (404)
  @ExceptionHandler(HttpClientErrorException.NotFound.class)
  public ResponseEntity<ApiResponse<Void>> handleTmdbNotFound(HttpClientErrorException.NotFound e) {
    ErrorCode errorCode = ErrorCode.MOVIE_NOT_FOUND;
    return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiResponse.error(errorCode.getMessage()));
  }

  // TMDB API 키 만료(401), 타임아웃, 5xx 등 그 외 외부 연동 실패
  @ExceptionHandler(RestClientException.class)
  public ResponseEntity<ApiResponse<Void>> handleExternalApiException(RestClientException e) {
    ErrorCode errorCode = ErrorCode.EXTERNAL_API_ERROR;
    return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiResponse.error(errorCode.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldError() != null
            ? e.getBindingResult().getFieldError().getDefaultMessage()
            : "입력값이 올바르지 않습니다.";
    return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
  }
}