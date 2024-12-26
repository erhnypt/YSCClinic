package com.yscclinic.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yscclinic.dto.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = createErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        ErrorResponse error = createErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        ErrorResponse error = createErrorResponse(
                HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse error = createErrorResponse(
                HttpStatus.UNAUTHORIZED, "Geçersiz kullanıcı adı veya şifre", request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse error = createErrorResponse(
                HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz bulunmamaktadır", request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponse error = createErrorResponse(
                HttpStatus.BAD_REQUEST, "Validasyon hatası", request.getRequestURI());
        error.setDetails(errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ErrorResponse error = createErrorResponse(
                HttpStatus.BAD_REQUEST, "Validasyon hatası", request.getRequestURI());
        error.setDetails(errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = createErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Beklenmeyen bir hata oluştu", ex);
        ErrorResponse error = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu", request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse createErrorResponse(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
