package com.yscclinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscclinic.dto.LoginRequest;
import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.repository.RoleRepository;
import com.yscclinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Role role;
    private String token;

    @BeforeEach
    void setUp() {
        // Role oluştur
        role = new Role();
        role.setName("ROLE_ADMIN");
        role = roleRepository.save(role);

        // User oluştur
        user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("admin@example.com");
        user.setFullName("Admin User");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        user = userRepository.save(user);

        // Login yap ve token al
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");

        try {
            String result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            token = objectMapper.readTree(result).get("data").get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Token alınamadı", e);
        }
    }

    @Test
    void getAllUsers_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].username").value(user.getUsername()));
    }

    @Test
    void getUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    void getUserByUsername_Success() throws Exception {
        mockMvc.perform(get("/api/users/username/" + user.getUsername())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    void updateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setUsername("updatedadmin");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFullName("Updated Admin");

        mockMvc.perform(put("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(updatedUser.getUsername()));
    }

    @Test
    void addRoleToUser_Success() throws Exception {
        Role newRole = new Role();
        newRole.setName("ROLE_USER");
        newRole = roleRepository.save(newRole);

        mockMvc.perform(post("/api/users/" + user.getId() + "/roles/" + newRole.getName())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roles[*].name").value(newRole.getName()));
    }

    @Test
    void removeRoleFromUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/" + user.getId() + "/roles/" + role.getName())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roles").isEmpty());
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Kullanıcı başarıyla silindi"));
    }
}
