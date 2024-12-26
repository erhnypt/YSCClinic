package com.yscclinic.service;

import com.yscclinic.entity.Patient;
import com.yscclinic.exception.ResourceNotFoundException;
import com.yscclinic.repository.PatientRepository;
import com.yscclinic.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

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
    void createPatient_Success() {
        when(patientRepository.existsByTcNo(patient.getTcNo())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient savedPatient = patientService.createPatient(patient);

        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getTcNo()).isEqualTo(patient.getTcNo());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_DuplicateTcNo_ThrowsException() {
        when(patientRepository.existsByTcNo(patient.getTcNo())).thenReturn(true);

        assertThatThrownBy(() -> patientService.createPatient(patient))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bu TC kimlik numarası zaten kayıtlı");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void getPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient foundPatient = patientService.getPatientById(1L);

        assertThat(foundPatient).isNotNull();
        assertThat(foundPatient.getId()).isEqualTo(1L);
        assertThat(foundPatient.getTcNo()).isEqualTo(patient.getTcNo());
    }

    @Test
    void getPatientById_NotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hasta bulunamadı");
    }

    @Test
    void getPatientByTcNo_Success() {
        when(patientRepository.findByTcNo(patient.getTcNo())).thenReturn(Optional.of(patient));

        Patient foundPatient = patientService.getPatientByTcNo(patient.getTcNo());

        assertThat(foundPatient).isNotNull();
        assertThat(foundPatient.getTcNo()).isEqualTo(patient.getTcNo());
    }

    @Test
    void getPatientByTcNo_NotFound_ThrowsException() {
        when(patientRepository.findByTcNo(patient.getTcNo())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientByTcNo(patient.getTcNo()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hasta bulunamadı");
    }

    @Test
    void getAllPatients_Success() {
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setTcNo("98765432109");

        List<Patient> patients = Arrays.asList(patient, patient2);
        Page<Patient> page = new PageImpl<>(patients);
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Patient> foundPatients = patientService.getAllPatients(Pageable.unpaged());

        assertThat(foundPatients).isNotNull();
        assertThat(foundPatients.getContent()).hasSize(2);
        assertThat(foundPatients.getContent()).contains(patient, patient2);
    }

    @Test
    void updatePatient_Success() {
        Patient updatedPatient = new Patient();
        updatedPatient.setTcNo("98765432109");
        updatedPatient.setFullName("Updated Patient");
        updatedPatient.setPhone("5559876543");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByTcNo(updatedPatient.getTcNo())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        Patient result = patientService.updatePatient(1L, updatedPatient);

        assertThat(result).isNotNull();
        assertThat(result.getTcNo()).isEqualTo(updatedPatient.getTcNo());
        assertThat(result.getFullName()).isEqualTo(updatedPatient.getFullName());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void deletePatient_Success() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
    }

    @Test
    void deletePatient_NotFound_ThrowsException() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> patientService.deletePatient(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hasta bulunamadı");

        verify(patientRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchPatients_Success() {
        String searchTerm = "test";
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setTcNo("98765432109");

        List<Patient> patients = Arrays.asList(patient, patient2);
        Page<Patient> page = new PageImpl<>(patients);
        when(patientRepository.findByFullNameContainingIgnoreCase(eq(searchTerm), any(Pageable.class)))
                .thenReturn(page);

        Page<Patient> foundPatients = patientService.searchPatients(searchTerm, Pageable.unpaged());

        assertThat(foundPatients).isNotNull();
        assertThat(foundPatients.getContent()).hasSize(2);
        assertThat(foundPatients.getContent()).contains(patient, patient2);
    }
}
