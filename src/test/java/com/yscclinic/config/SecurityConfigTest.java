package com.yscclinic.config;

import com.yscclinic.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoints_NoAuthentication_Success() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/signup"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoints_NoAuthentication_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoints_UserRole_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoints_AdminRole_Success() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userEndpoints_UserRole_Success() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidJwt_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }
}
