package com.hospital.surgicalday.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookCaseRequest {

    @NotNull
    private LocalDate surgeryDate;

    @NotNull
    private LocalTime startTime;

    @Min(15)
    private Integer durationMinutes;

    @NotNull
    private Long patientId;

    @NotNull
    private Long procedureId;

    @NotNull
    private Long theatreId;

    @NotNull
    private Long surgeonId;
}
