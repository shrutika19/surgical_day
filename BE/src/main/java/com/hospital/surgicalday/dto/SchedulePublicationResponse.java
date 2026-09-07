package com.hospital.surgicalday.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class SchedulePublicationResponse {
    LocalDate surgeryDate;
    int caseCount;
    boolean published;
    LocalDateTime publishedAt;
}
