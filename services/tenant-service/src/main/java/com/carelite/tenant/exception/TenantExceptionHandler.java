package com.carelite.tenant.exception;

import com.carelite.common.ApiError;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class TenantExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationError(MethodArgumentNotValidException ex) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(";"));

    return ResponseEntity.badRequest().body(ApiError.of("VALIDATION_FAILED", errorMessage, null));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ApiError> missingHeader(MissingRequestHeaderException ex) {
    return ResponseEntity.badRequest()
        .body(ApiError.of("MISSING_HEADER", ex.getHeaderName() + " is required", null));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> constraintViolation(ConstraintViolationException ex) {
    return ResponseEntity.badRequest()
        .body(ApiError.of("VALIDATION_FAILED", ex.getMessage(), null));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiError> handlerMethodValidation(HandlerMethodValidationException ex) {
    String errorMessage =
        ex.getAllErrors().stream()
            .map(this::formatValidationError)
            .collect(Collectors.joining(";"));

    if (errorMessage.isBlank()) {
      errorMessage = ex.getReason();
    }

    return ResponseEntity.badRequest().body(ApiError.of("VALIDATION_FAILED", errorMessage, null));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> unreadableBody(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest()
        .body(ApiError.of("INVALID_JSON", "Request body is missing or invalid JSON", null));
  }

  @ExceptionHandler(DuplicateTenantSlugException.class)
  public ResponseEntity<ApiError> duplicateSlug(DuplicateTenantSlugException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of("TENANT_SLUG_EXISTS", ex.getMessage(), null));
  }

  @ExceptionHandler(IdempotencyConflictException.class)
  public ResponseEntity<ApiError> idempotencyConflict(IdempotencyConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of("IDEMPOTENCY_CONFLICT", ex.getMessage(), null));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiError> noResource(NoResourceFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of("NOT_FOUND", ex.getMessage(), null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> fallback(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiError.of(
                "INTERNAL_ERROR", ex.getClass().getSimpleName() + ": " + ex.getMessage(), null));
  }

  private String formatValidationError(MessageSourceResolvable error) {
    if (error instanceof FieldError fieldError) {
      return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }
    if (error instanceof ObjectError objectError) {
      return objectError.getDefaultMessage();
    }
    return error.getDefaultMessage();
  }
}
