package com.hospital.surgicalday.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String message;
}
