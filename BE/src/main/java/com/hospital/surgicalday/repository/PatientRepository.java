package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select patient from Patient patient where patient.id = :id")
    Optional<Patient> findWithLockById(@Param("id") Long id);
}
