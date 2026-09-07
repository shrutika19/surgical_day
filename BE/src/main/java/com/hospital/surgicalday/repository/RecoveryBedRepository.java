package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.RecoveryBed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecoveryBedRepository extends JpaRepository<RecoveryBed, Long> {
}
