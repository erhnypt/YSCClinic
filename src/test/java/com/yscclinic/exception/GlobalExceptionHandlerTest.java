package com.yscclinic.exception;

import com.yscclinic.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleResourceNotFoundException() {
        String message = "Resource not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).isEqualTo("Bu işlem için yetkiniz bulunmamaktadır");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void handleAuthenticationException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Geçersiz kullanıcı adı veya şifre");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void handleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Validasyon hatası");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getDetails()).isNotEmpty();
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("Beklenmeyen bir hata oluştu");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
