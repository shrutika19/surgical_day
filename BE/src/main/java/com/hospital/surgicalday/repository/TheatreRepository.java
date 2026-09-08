package com.hospital.surgicalday.repository;

import com.hospital.surgicalday.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select theatre from Theatre theatre where theatre.id = :id")
	Optional<Theatre> findWithLockById(@Param("id") Long id);
}
