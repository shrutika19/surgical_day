package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Surgeon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface SurgeonRepository extends JpaRepository<Surgeon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select surgeon from Surgeon surgeon where surgeon.id = :id")
    Optional<Surgeon> findWithLockById(@Param("id") Long id);
}
