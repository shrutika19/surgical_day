package com.hospital.surgicalday.service;

import com.hospital.surgicalday.dto.BillResponse;
import com.hospital.surgicalday.dto.SurgicalCaseResponse;
import com.hospital.surgicalday.dto.UpdateStatusRequest;
import com.hospital.surgicalday.exception.ResourceNotFoundException;
import com.hospital.surgicalday.model.*;
import com.hospital.surgicalday.repository.BillRepository;
import com.hospital.surgicalday.repository.SurgicalCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseLifecycleService {

        private static final BigDecimal THEATRE_FEE = new BigDecimal("450.00");
        private static final BigDecimal RECOVERY_FEE = new BigDecimal("175.00");

        private final SurgicalCaseRepository surgicalCaseRepository;
        private final BillRepository billRepository;

        @Transactional
        public SurgicalCaseResponse updateStatus(Long caseId, UpdateStatusRequest request) {
                SurgicalCase surgicalCase = surgicalCaseRepository.findWithLockById(caseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

                CaseStatus current = surgicalCase.getStatus();
                CaseStatus next = request.getStatus();

                if (current == CaseStatus.DISCHARGED) {
                        throw new IllegalStateException("Case is already discharged");
                }

                validateTransition(current, next);

                surgicalCase.setStatus(next);
                if (next == CaseStatus.IN_RECOVERY) {
                        surgicalCase.setRecoveryStartedAt(LocalDateTime.now());
                }
                if (next == CaseStatus.DISCHARGED) {
                        surgicalCase.setDischargedAt(LocalDateTime.now());
                        if (surgicalCase.getRecoveryStartedAt() == null) {
                                surgicalCase.setRecoveryStartedAt(LocalDateTime.now().minusMinutes(
                                                surgicalCase.getProcedure().getRecoveryMinutes()));
                        }
                        createBill(surgicalCase);
                }

                return CaseMapper.toResponse(surgicalCaseRepository.save(surgicalCase));
        }

        @Transactional(readOnly = true)
        public BillResponse getBill(Long caseId) {
                Bill bill = billRepository.findBySurgicalCaseId(caseId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No bill found for case " + caseId + ". Discharge the patient first."));
                return toBillResponse(bill);
        }

        private void validateTransition(CaseStatus current, CaseStatus next) {
                boolean allowed = (current == CaseStatus.SCHEDULED && next == CaseStatus.IN_THEATRE)
                                || (current == CaseStatus.IN_THEATRE && next == CaseStatus.IN_RECOVERY)
                                || (current == CaseStatus.IN_RECOVERY && next == CaseStatus.DISCHARGED);
                if (!allowed) {
                        throw new IllegalStateException(
                                        "Cannot move case from " + current + " to " + next
                                                        + ". Allowed path: SCHEDULED → IN_THEATRE → IN_RECOVERY → DISCHARGED");
                }
        }

        private void createBill(SurgicalCase surgicalCase) {
                if (billRepository.findBySurgicalCaseId(surgicalCase.getId()).isPresent()) {
                        return;
                }

                Bill bill = Bill.builder()
                                .surgicalCase(surgicalCase)
                                .createdAt(LocalDateTime.now())
                                .totalAmount(BigDecimal.ZERO)
                                .lines(new ArrayList<>())
                                .build();

                List<BillLine> lines = List.of(
                                BillLine.builder()
                                                .bill(bill)
                                                .description(surgicalCase.getProcedure().getName())
                                                .amount(surgicalCase.getProcedure().getBasePrice())
                                                .build(),
                                BillLine.builder()
                                                .bill(bill)
                                                .description("Theatre charge — " + surgicalCase.getTheatre().getName())
                                                .amount(THEATRE_FEE)
                                                .build(),
                                BillLine.builder()
                                                .bill(bill)
                                                .description("Recovery bed")
                                                .amount(RECOVERY_FEE)
                                                .build());

                bill.getLines().addAll(lines);
                BigDecimal total = lines.stream()
                                .map(BillLine::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                bill.setTotalAmount(total);
                billRepository.save(bill);
        }

        private BillResponse toBillResponse(Bill bill) {
                return BillResponse.builder()
                                .id(bill.getId())
                                .surgicalCaseId(bill.getSurgicalCase().getId())
                                .patientName(bill.getSurgicalCase().getPatient().getFullName())
                                .createdAt(bill.getCreatedAt())
                                .totalAmount(bill.getTotalAmount())
                                .lines(bill.getLines().stream()
                                                .map(line -> BillResponse.BillLineResponse.builder()
                                                                .description(line.getDescription())
                                                                .amount(line.getAmount())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }
}
