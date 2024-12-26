package com.yscclinic.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getAuthentication(token)).thenReturn(authentication);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider).validateToken(token);
        verify(tokenProvider).getAuthentication(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoAuthHeader_ContinuesChain() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider, never()).validateToken(anyString());
        verify(tokenProvider, never()).getAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ContinuesChain() throws Exception {
        // Arrange
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider).validateToken(token);
        verify(tokenProvider, never()).getAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidAuthHeaderFormat_ContinuesChain() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider, never()).validateToken(anyString());
        verify(tokenProvider, never()).getAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }
}
