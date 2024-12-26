package com.yscclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscclinic.entity.Patient;
import com.yscclinic.service.PatientService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setTcNo("12345678901");
        patient.setFullName("Test Patient");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setGender(Patient.Gender.ERKEK);
        patient.setPhone("5551234567");
        patient.setEmail("patient@example.com");
        patient.setAddress("Test Address");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPatient_Success() throws Exception {
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(patient.getTcNo()))
                .andExpect(jsonPath("$.data.fullName").value(patient.getFullName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPatientById_Success() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patient);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(patient.getTcNo()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPatientByTcNo_Success() throws Exception {
        when(patientService.getPatientByTcNo(patient.getTcNo())).thenReturn(patient);

        mockMvc.perform(get("/api/patients/tc/" + patient.getTcNo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(patient.getTcNo()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPatients_Success() throws Exception {
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setTcNo("98765432109");

        List<Patient> patients = Arrays.asList(patient, patient2);
        Page<Patient> page = new PageImpl<>(patients);
        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].tcNo").value(patient.getTcNo()))
                .andExpect(jsonPath("$.data.content[1].tcNo").value(patient2.getTcNo()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePatient_Success() throws Exception {
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setTcNo("98765432109");
        updatedPatient.setFullName("Updated Patient");

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(updatedPatient);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(updatedPatient.getTcNo()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePatient_Success() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hasta başarıyla silindi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchPatients_Success() throws Exception {
        String searchTerm = "test";
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setTcNo("98765432109");

        List<Patient> patients = Arrays.asList(patient, patient2);
        Page<Patient> page = new PageImpl<>(patients);
        when(patientService.searchPatients(eq(searchTerm), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/patients/search")
                .param("query", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].tcNo").value(patient.getTcNo()))
                .andExpect(jsonPath("$.data.content[1].tcNo").value(patient2.getTcNo()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessDenied_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isForbidden());
    }
}
