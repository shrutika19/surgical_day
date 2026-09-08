package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.CaseStatus;
import com.hospital.surgicalday.model.SurgicalCase;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SurgicalCaseRepository extends JpaRepository<SurgicalCase, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select surgicalCase from SurgicalCase surgicalCase where surgicalCase.id = :id")
    java.util.Optional<SurgicalCase> findWithLockById(@Param("id") Long id);

    List<SurgicalCase> findBySurgeryDateOrderByStartTimeAsc(LocalDate surgeryDate);

    List<SurgicalCase> findBySurgeryDateAndSurgeonId(LocalDate surgeryDate, Long surgeonId);

    List<SurgicalCase> findBySurgeryDateAndTheatreId(LocalDate surgeryDate, Long theatreId);

    List<SurgicalCase> findBySurgeryDateAndPatientId(LocalDate surgeryDate, Long patientId);

    List<SurgicalCase> findBySurgeryDateAndStatusIn(LocalDate surgeryDate, List<CaseStatus> statuses);
}
