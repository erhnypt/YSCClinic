package com.yscclinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yscclinic.entity.Patient;

public interface PatientService {

    Patient createPatient(Patient patient);

    Patient getPatientById(Long id);

    Patient getPatientByTcNo(String tcNo);

    Patient getPatientByEmail(String email);

    Patient getPatientByPhone(String phone);

    Page<Patient> getAllPatients(Pageable pageable);

    Page<Patient> searchPatients(String search, Pageable pageable);

    Page<Patient> searchPatientsByGender(Patient.Gender gender, String search, Pageable pageable);

    Patient updatePatient(Long id, Patient patient);

    void deletePatient(Long id);

    boolean existsByTcNo(String tcNo);
}
