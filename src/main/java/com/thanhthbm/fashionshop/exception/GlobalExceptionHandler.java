package com.thanhthbm.fashionshop.exception;

import com.thanhthbm.fashionshop.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j; // 1. Cần thêm thư viện logging (Lombok)
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());

    ApiResponse<Object> response = ApiResponse.fail(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage()
    );
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    log.warn("Validation error: {}", errors);

    ApiResponse<Object> response = ApiResponse.fail(
        HttpStatus.BAD_REQUEST.value(),
        "Validation failed"
    );

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Object>> handleJsonErrors(HttpMessageNotReadableException ex) {
    log.error("Malformed JSON request: {}", ex.getMessage());

    ApiResponse<Object> response = ApiResponse.fail(
        HttpStatus.BAD_REQUEST.value(),
        "Malformed JSON request. Please check your request body."
    );
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());

    ApiResponse<Object> response = ApiResponse.fail(
        HttpStatus.FORBIDDEN.value(),
        "You do not have permission to access this resource."
    );
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex);

    ApiResponse<Object> response = ApiResponse.error(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "An internal server error occurred. Please contact support."
    );

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}