package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Long> {
}
