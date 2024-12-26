package com.yscclinic.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000; // 1 day

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", EXPIRATION);
        tokenProvider.init();
    }

    @Test
    void createToken_ValidAuthentication_ReturnsToken() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Act
        String token = tokenProvider.createToken(authentication);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = tokenProvider.createToken(authentication);

        // Act
        boolean isValid = tokenProvider.validateToken(token);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalidToken";

        // Act
        boolean isValid = tokenProvider.validateToken(invalidToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void getAuthentication_ValidToken_ReturnsAuthentication() {
        // Arrange
        Authentication originalAuth = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = tokenProvider.createToken(originalAuth);

        // Act
        Authentication authentication = tokenProvider.getAuthentication(token);

        // Assert
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("testUser");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }
}
