package com.yscclinic.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignupRequestTest {

    private Validator validator;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setPassword("password123");
        signupRequest.setEmail("test@example.com");
        signupRequest.setFullName("Test User");
    }

    @Test
    void validSignupRequest_NoViolations() {
        var violations = validator.validate(signupRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void emptyUsername_HasViolation() {
        signupRequest.setUsername("");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Kullanıcı adı boş olamaz");
    }

    @Test
    void shortUsername_HasViolation() {
        signupRequest.setUsername("ab");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Kullanıcı adı 3-50 karakter arasında olmalıdır");
    }

    @Test
    void emptyPassword_HasViolation() {
        signupRequest.setPassword("");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Şifre boş olamaz");
    }

    @Test
    void shortPassword_HasViolation() {
        signupRequest.setPassword("12345");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Şifre en az 6 karakter olmalıdır");
    }

    @Test
    void invalidEmail_HasViolation() {
        signupRequest.setEmail("invalid-email");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Geçerli bir e-posta adresi giriniz");
    }

    @Test
    void emptyEmail_HasViolation() {
        signupRequest.setEmail("");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("E-posta adresi boş olamaz");
    }

    @Test
    void emptyFullName_HasViolation() {
        signupRequest.setFullName("");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Ad Soyad boş olamaz");
    }

    @Test
    void shortFullName_HasViolation() {
        signupRequest.setFullName("ab");
        var violations = validator.validate(signupRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Ad Soyad 3-100 karakter arasında olmalıdır");
    }
}
