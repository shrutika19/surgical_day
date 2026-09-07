package com.hospital.surgicalday.dto;

import com.hospital.surgicalday.model.CaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private CaseStatus status;
}
