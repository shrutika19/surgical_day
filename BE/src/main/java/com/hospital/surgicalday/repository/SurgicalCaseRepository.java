package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.CaseStatus;
import com.hospital.surgicalday.model.SurgicalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SurgicalCaseRepository extends JpaRepository<SurgicalCase, Long> {

    List<SurgicalCase> findBySurgeryDateOrderByStartTimeAsc(LocalDate surgeryDate);

    List<SurgicalCase> findBySurgeryDateAndSurgeonId(LocalDate surgeryDate, Long surgeonId);

    List<SurgicalCase> findBySurgeryDateAndStatusIn(LocalDate surgeryDate, List<CaseStatus> statuses);
}
