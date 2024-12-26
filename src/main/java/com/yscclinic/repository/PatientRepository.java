package com.yscclinic.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yscclinic.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByTcNo(String tcNo);

    boolean existsByTcNo(String tcNo);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPhone(String phone);

    @Query("SELECT p FROM Patient p WHERE "
            + "LOWER(p.tcNo) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(p.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Patient> searchPatients(String search, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE "
            + "p.gender = :gender AND "
            + "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(p.tcNo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> searchPatientsByGender(Patient.Gender gender, String search, Pageable pageable);

    Page<Patient> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
