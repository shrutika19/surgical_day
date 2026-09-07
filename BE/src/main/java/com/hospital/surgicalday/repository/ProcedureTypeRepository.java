package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.ProcedureType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedureTypeRepository extends JpaRepository<ProcedureType, Long> {
}
