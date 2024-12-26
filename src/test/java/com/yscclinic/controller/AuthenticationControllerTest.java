package com.yscclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscclinic.dto.LoginRequest;
import com.yscclinic.dto.SignupRequest;
import com.yscclinic.dto.UserDto;
import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.security.JwtTokenProvider;
import com.yscclinic.service.RoleService;
import com.yscclinic.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    private LoginRequest loginRequest;
    private UserDto userDto;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("password");
        userDto.setEmail("newuser@example.com");
        userDto.setFullName("New User");

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
    }

    @Test
    void login_Success() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(tokenProvider.createToken(authentication)).thenReturn("jwt.token.here");
        when(userService.getUserByUsername(loginRequest.getUsername())).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    void signup_Success() throws Exception {
        when(roleService.getRoleByName("ROLE_USER")).thenReturn(role);
        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    void signup_DuplicateUsername_ReturnsBadRequest() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new IllegalArgumentException("Bu kullanıcı adı zaten mevcut"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bu kullanıcı adı zaten mevcut"));
    }

    @Test
    void signup_DuplicateEmail_ReturnsBadRequest() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new IllegalArgumentException("Bu e-posta adresi zaten mevcut"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bu e-posta adresi zaten mevcut"));
    }
}
