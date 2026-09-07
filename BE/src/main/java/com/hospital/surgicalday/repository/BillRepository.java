package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findBySurgicalCaseId(Long surgicalCaseId);
}
