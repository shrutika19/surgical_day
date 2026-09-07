package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
}
