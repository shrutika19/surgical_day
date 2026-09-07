package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Surgeon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurgeonRepository extends JpaRepository<Surgeon, Long> {
}
