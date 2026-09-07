package com.hospital.surgicalday.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BillResponse {

    private Long id;
    private Long surgicalCaseId;
    private String patientName;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private List<BillLineResponse> lines;

    @Data
    @Builder
    public static class BillLineResponse {
        private String description;
        private BigDecimal amount;
    }
}
