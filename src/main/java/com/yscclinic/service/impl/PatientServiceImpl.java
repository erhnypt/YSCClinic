package com.yscclinic.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yscclinic.entity.Patient;
import com.yscclinic.repository.PatientRepository;
import com.yscclinic.service.PatientService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public Patient createPatient(Patient patient) {
        if (patientRepository.existsByTcNo(patient.getTcNo())) {
            throw new IllegalArgumentException("Bu TC kimlik numarası zaten kayıtlı: " + patient.getTcNo());
        }
        return patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hasta bulunamadı: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientByTcNo(String tcNo) {
        return patientRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new EntityNotFoundException("Hasta bulunamadı: " + tcNo));
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Hasta bulunamadı: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientByPhone(String phone) {
        return patientRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Hasta bulunamadı: " + phone));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> searchPatients(String search, Pageable pageable) {
        return patientRepository.searchPatients(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> searchPatientsByGender(Patient.Gender gender, String search, Pageable pageable) {
        return patientRepository.searchPatientsByGender(gender, search, pageable);
    }

    @Override
    public Patient updatePatient(Long id, Patient patient) {
        Patient existingPatient = getPatientById(id);

        if (!existingPatient.getTcNo().equals(patient.getTcNo())
                && patientRepository.existsByTcNo(patient.getTcNo())) {
            throw new IllegalArgumentException("Bu TC kimlik numarası zaten kayıtlı: " + patient.getTcNo());
        }

        existingPatient.setTcNo(patient.getTcNo());
        existingPatient.setFullName(patient.getFullName());
        existingPatient.setBirthDate(patient.getBirthDate());
        existingPatient.setGender(patient.getGender());
        existingPatient.setPhone(patient.getPhone());
        existingPatient.setEmail(patient.getEmail());
        existingPatient.setAddress(patient.getAddress());

        return patientRepository.save(existingPatient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Hasta bulunamadı: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTcNo(String tcNo) {
        return patientRepository.existsByTcNo(tcNo);
    }
}
