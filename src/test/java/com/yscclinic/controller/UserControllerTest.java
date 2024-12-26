package com.yscclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscclinic.dto.UserDto;
import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEmail("test@example.com");
        userDto.setFullName("Test User");

        when(userService.createUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByUsername_Success() throws Exception {
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        mockMvc.perform(get("/api/users/username/" + user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");

        List<User> users = Arrays.asList(user, user2);
        Page<User> page = new PageImpl<>(users);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].username").value(user.getUsername()))
                .andExpect(jsonPath("$.data.content[1].username").value(user2.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("updateduser");
        userDto.setPassword("newpassword");
        userDto.setEmail("updated@example.com");
        userDto.setFullName("Updated User");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Kullanıcı başarıyla silindi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addRoleToUser_Success() throws Exception {
        doNothing().when(userService).addRoleToUser(eq(1L), eq("ROLE_ADMIN"));

        mockMvc.perform(post("/api/users/1/roles/ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeRoleFromUser_Success() throws Exception {
        doNothing().when(userService).removeRoleFromUser(eq(1L), eq("ROLE_USER"));

        mockMvc.perform(delete("/api/users/1/roles/ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessDenied_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }
}
