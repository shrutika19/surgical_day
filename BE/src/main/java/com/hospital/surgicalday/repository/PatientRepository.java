package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
