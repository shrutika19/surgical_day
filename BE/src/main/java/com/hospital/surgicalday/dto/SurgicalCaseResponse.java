package com.hospital.surgicalday.dto;

import com.hospital.surgicalday.model.CaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
public class SurgicalCaseResponse {

    private Long id;
    private LocalDate surgeryDate;
    private LocalTime startTime;
    private int durationMinutes;
    private CaseStatus status;
    private Long patientId;
    private String patientName;
    private Long procedureId;
    private String procedureName;
    private Long theatreId;
    private String theatreName;
    private String buildingName;
    private Long surgeonId;
    private String surgeonName;
    private LocalDateTime recoveryStartedAt;
    private LocalDateTime dischargedAt;
    private Set<String> requiredEquipment;
    private Set<String> theatreEquipment;
}
