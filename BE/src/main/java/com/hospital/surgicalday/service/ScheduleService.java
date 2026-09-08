package com.hospital.surgicalday.service;

import com.hospital.surgicalday.dto.BookCaseRequest;
import com.hospital.surgicalday.dto.RecoveryOccupancyResponse;
import com.hospital.surgicalday.dto.SchedulePublicationResponse;
import com.hospital.surgicalday.dto.SurgicalCaseResponse;
import com.hospital.surgicalday.exception.ResourceNotFoundException;
import com.hospital.surgicalday.exception.ScheduleConflictException;
import com.hospital.surgicalday.model.*;
import com.hospital.surgicalday.repository.*;
import com.hospital.surgicalday.scheduling.TimeInterval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final SurgicalCaseRepository surgicalCaseRepository;
    private final PatientRepository patientRepository;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final TheatreRepository theatreRepository;
    private final SurgeonRepository surgeonRepository;
    private final RecoveryBedRepository recoveryBedRepository;

    @Transactional(readOnly = true)
    public List<SurgicalCaseResponse> listByDate(LocalDate date) {
        return surgicalCaseRepository.findBySurgeryDateOrderByStartTimeAsc(date).stream()
                .map(CaseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SurgicalCaseResponse book(BookCaseRequest request) {
        Patient patient = patientRepository.findWithLockById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));
        ProcedureType procedure = procedureTypeRepository.findById(request.getProcedureId())
                .orElseThrow(() -> new ResourceNotFoundException("Procedure not found: " + request.getProcedureId()));
        Theatre theatre = theatreRepository.findWithLockById(request.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found: " + request.getTheatreId()));
        Surgeon surgeon = surgeonRepository.findWithLockById(request.getSurgeonId())
                .orElseThrow(() -> new ResourceNotFoundException("Surgeon not found: " + request.getSurgeonId()));

        int duration = request.getDurationMinutes() != null
                ? request.getDurationMinutes()
                : procedure.getDefaultMinutes();

        validateSurgeryDuration(duration);

        LocalDateTime start = LocalDateTime.of(request.getSurgeryDate(), request.getStartTime());
        LocalDateTime end = start.plusMinutes(duration);

        validateSurgeonAvailability(request.getSurgeryDate(), surgeon.getId(), start, end);
        validatePatientAvailability(request.getSurgeryDate(), patient.getId(), start, end);
        validateTheatreAvailability(request.getSurgeryDate(), theatre.getId(), start, end);
        validateTheatreEquipment(theatre, procedure);
        validateRecoveryCapacity(request.getSurgeryDate(), start.plusMinutes(duration),
                procedure.getRecoveryMinutes());

        SurgicalCase surgicalCase = SurgicalCase.builder()
                .surgeryDate(request.getSurgeryDate())
                .startTime(request.getStartTime())
                .durationMinutes(duration)
                .status(CaseStatus.SCHEDULED)
                .patient(patient)
                .procedure(procedure)
                .theatre(theatre)
                .surgeon(surgeon)
                .build();

        return CaseMapper.toResponse(surgicalCaseRepository.save(surgicalCase));
    }

    @Transactional(readOnly = true)
    public SchedulePublicationResponse publish(LocalDate date) {
        List<SurgicalCase> cases = surgicalCaseRepository.findBySurgeryDateOrderByStartTimeAsc(date);
        for (SurgicalCase surgicalCase : cases) {
            validateSurgeryDuration(surgicalCase.getDurationMinutes());
            validateTheatreEquipment(surgicalCase.getTheatre(), surgicalCase.getProcedure());
            validateSurgeonAvailability(date, surgicalCase.getSurgeon().getId(),
                    surgicalCase.surgeryStartDateTime(), surgicalCase.surgeryEndDateTime(), surgicalCase.getId());
            validatePatientAvailability(date, surgicalCase.getPatient().getId(),
                    surgicalCase.surgeryStartDateTime(), surgicalCase.surgeryEndDateTime(), surgicalCase.getId());
            validateTheatreAvailability(date, surgicalCase.getTheatre().getId(),
                    surgicalCase.surgeryStartDateTime(), surgicalCase.surgeryEndDateTime(), surgicalCase.getId());
            validateRecoveryCapacity(date, surgicalCase.projectedRecoveryStart(),
                    surgicalCase.getProcedure().getRecoveryMinutes(), surgicalCase.getId());
        }
        return SchedulePublicationResponse.builder()
                .surgeryDate(date)
                .caseCount(cases.size())
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public RecoveryOccupancyResponse occupancy(LocalDate date, LocalTime at) {
        LocalDateTime instant = LocalDateTime.of(date, at);
        int capacity = (int) recoveryBedRepository.count();
        int occupied = countProjectedOccupancy(date, instant, null);
        return RecoveryOccupancyResponse.builder()
                .at(instant)
                .bedCapacity(capacity)
                .occupied(occupied)
                .full(occupied >= capacity)
                .build();
    }

    private void validateSurgeonAvailability(LocalDate date, Long surgeonId,
            LocalDateTime start, LocalDateTime end) {
        validateSurgeonAvailability(date, surgeonId, start, end, null);
    }

    private void validateSurgeonAvailability(LocalDate date, Long surgeonId,
            LocalDateTime start, LocalDateTime end, Long excludeCaseId) {
        List<SurgicalCase> existing = surgicalCaseRepository.findBySurgeryDateAndSurgeonId(date, surgeonId);
        for (SurgicalCase other : existing) {
            if (excludeCaseId != null && excludeCaseId.equals(other.getId())) {
                continue;
            }
            if (other.getStatus() == CaseStatus.DISCHARGED) {
                continue;
            }
            LocalDateTime otherStart = other.surgeryStartDateTime();
            LocalDateTime otherEnd = other.surgeryEndDateTime();
            if (new TimeInterval(start, end).overlaps(new TimeInterval(otherStart, otherEnd))) {
                throw new ScheduleConflictException(
                        "Surgeon " + other.getSurgeon().getName()
                                + " is already booked from " + other.getStartTime()
                                + " for " + other.getDurationMinutes() + " minutes");
            }
        }
    }

    private void validateTheatreAvailability(LocalDate date, Long theatreId,
            LocalDateTime start, LocalDateTime end) {
        validateTheatreAvailability(date, theatreId, start, end, null);
    }

    private void validateTheatreAvailability(LocalDate date, Long theatreId,
            LocalDateTime start, LocalDateTime end, Long excludeCaseId) {
        List<SurgicalCase> existing = surgicalCaseRepository.findBySurgeryDateAndTheatreId(date, theatreId);
        for (SurgicalCase other : existing) {
            if (excludeCaseId != null && excludeCaseId.equals(other.getId())) {
                continue;
            }
            if (other.getStatus() == CaseStatus.DISCHARGED) {
                continue;
            }
            LocalDateTime otherStart = other.surgeryStartDateTime();
            LocalDateTime otherEnd = other.surgeryEndDateTime();
            if (new TimeInterval(start, end).overlaps(new TimeInterval(otherStart, otherEnd))) {
                throw new ScheduleConflictException(
                        "Theatre " + other.getTheatre().getName()
                                + " is already booked from " + other.getStartTime()
                                + " for " + other.getDurationMinutes() + " minutes");
            }
        }
    }

    private void validatePatientAvailability(LocalDate date, Long patientId,
            LocalDateTime start, LocalDateTime end) {
        validatePatientAvailability(date, patientId, start, end, null);
    }

    private void validatePatientAvailability(LocalDate date, Long patientId,
            LocalDateTime start, LocalDateTime end, Long excludeCaseId) {
        List<SurgicalCase> existing = surgicalCaseRepository.findBySurgeryDateAndPatientId(date, patientId);
        for (SurgicalCase other : existing) {
            if (excludeCaseId != null && excludeCaseId.equals(other.getId())) {
                continue;
            }
            if (other.getStatus() == CaseStatus.DISCHARGED) {
                continue;
            }
                if (new TimeInterval(start, end).overlaps(
                    new TimeInterval(other.surgeryStartDateTime(), other.surgeryEndDateTime()))) {
                throw new ScheduleConflictException(
                        "Patient " + other.getPatient().getFullName()
                                + " is already booked from " + other.getStartTime()
                                + " for " + other.getDurationMinutes() + " minutes");
            }
        }
    }

    private void validateSurgeryDuration(int durationMinutes) {
        if (durationMinutes < 15) {
            throw new ScheduleConflictException("Surgery duration must be at least 15 minutes");
        }
    }

    private void validateTheatreEquipment(Theatre theatre, ProcedureType procedure) {
        Set<String> available = theatre.getEquipment() == null ? Set.of() : theatre.getEquipment();
        Set<String> required = procedure.getRequiredEquipment() == null
                ? Set.of()
                : procedure.getRequiredEquipment();
        Set<String> missing = new HashSet<>(required);
        missing.removeAll(available);
        if (!missing.isEmpty()) {
            throw new ScheduleConflictException(
                    "Theatre " + theatre.getName() + " is missing required equipment: "
                            + String.join(", ", missing));
        }
    }

    private void validateRecoveryCapacity(LocalDate date, LocalDateTime recoveryStart, int recoveryMinutes) {
        validateRecoveryCapacity(date, recoveryStart, recoveryMinutes, null);
    }

    private void validateRecoveryCapacity(LocalDate date, LocalDateTime recoveryStart, int recoveryMinutes,
            Long excludeCaseId) {
        LocalDateTime recoveryEnd = recoveryStart.plusMinutes(recoveryMinutes);
        int capacity = (int) recoveryBedRepository.count();
        if (capacity == 0) {
            throw new ScheduleConflictException("No recovery beds configured");
        }

        // Sample occupancy at recovery start and every 15 minutes through the recovery
        // window
        LocalDateTime probe = recoveryStart;
        while (!probe.isAfter(recoveryEnd)) {
            int occupied = countProjectedOccupancy(date, probe, excludeCaseId);
            if (occupied >= capacity) {
                throw new ScheduleConflictException(
                        "Recovery beds would be full at " + probe.toLocalTime()
                                + " (capacity " + capacity + ", already " + occupied + " patients)");
            }
            probe = probe.plusMinutes(15);
        }
    }

    /**
     * Counts cases that occupy a recovery bed at the given instant:
     * already IN_RECOVERY (until discharge), or SCHEDULED/IN_THEATRE whose
     * projected recovery window covers the instant.
     */
    int countProjectedOccupancy(LocalDate date, LocalDateTime instant, Long excludeCaseId) {
        List<SurgicalCase> cases = surgicalCaseRepository.findBySurgeryDateAndStatusIn(
                date,
                List.copyOf(EnumSet.of(CaseStatus.SCHEDULED, CaseStatus.IN_THEATRE, CaseStatus.IN_RECOVERY)));

        int count = 0;
        for (SurgicalCase c : cases) {
            if (excludeCaseId != null && excludeCaseId.equals(c.getId())) {
                continue;
            }
            LocalDateTime recStart;
            LocalDateTime recEnd;
            if (c.getStatus() == CaseStatus.IN_RECOVERY) {
                recStart = c.getRecoveryStartedAt() != null ? c.getRecoveryStartedAt() : c.projectedRecoveryStart();
                recEnd = recStart.plusMinutes(c.getProcedure().getRecoveryMinutes());
            } else {
                recStart = c.projectedRecoveryStart();
                recEnd = c.projectedRecoveryEnd();
            }
            if (!instant.isBefore(recStart) && instant.isBefore(recEnd)) {
                count++;
            }
        }
        return count;
    }
}
