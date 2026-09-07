package com.hospital.surgicalday.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecoveryOccupancyResponse {

    private LocalDateTime at;
    private int bedCapacity;
    private int occupied;
    private boolean full;
}
