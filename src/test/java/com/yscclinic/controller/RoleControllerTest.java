package com.yscclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscclinic.dto.RestResponse;
import com.yscclinic.entity.Role;
import com.yscclinic.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;
    private List<Role> roles;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        roles = Arrays.asList(role);
    }

    @Test
    void createRole_Success() throws Exception {
        when(roleService.createRole(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(role.getName()));
    }

    @Test
    void getRoleById_Success() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(role);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(role.getName()));
    }

    @Test
    void getRoleByName_Success() throws Exception {
        when(roleService.getRoleByName("ROLE_USER")).thenReturn(role);

        mockMvc.perform(get("/api/roles/name/ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(role.getName()));
    }

    @Test
    void getAllRoles_Success() throws Exception {
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value(role.getName()));
    }

    @Test
    void updateRole_Success() throws Exception {
        when(roleService.updateRole(any(Long.class), any(Role.class))).thenReturn(role);

        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(role.getName()));
    }

    @Test
    void deleteRole_Success() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
