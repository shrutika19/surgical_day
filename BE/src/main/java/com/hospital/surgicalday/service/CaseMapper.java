package com.hospital.surgicalday.service;

import com.hospital.surgicalday.dto.SurgicalCaseResponse;
import com.hospital.surgicalday.model.SurgicalCase;

public final class CaseMapper {

    private CaseMapper() {
    }

    public static SurgicalCaseResponse toResponse(SurgicalCase c) {
        return SurgicalCaseResponse.builder()
                .id(c.getId())
                .surgeryDate(c.getSurgeryDate())
                .startTime(c.getStartTime())
                .durationMinutes(c.getDurationMinutes())
                .status(c.getStatus())
                .patientId(c.getPatient().getId())
                .patientName(c.getPatient().getFullName())
                .procedureId(c.getProcedure().getId())
                .procedureName(c.getProcedure().getName())
                .theatreId(c.getTheatre().getId())
                .theatreName(c.getTheatre().getName())
                .buildingName(c.getTheatre().getBuilding().getName())
                .surgeonId(c.getSurgeon().getId())
                .surgeonName(c.getSurgeon().getName())
                .recoveryStartedAt(c.getRecoveryStartedAt())
                .dischargedAt(c.getDischargedAt())
                .requiredEquipment(c.getProcedure().getRequiredEquipment())
                .theatreEquipment(c.getTheatre().getEquipment())
                .build();
    }
}
