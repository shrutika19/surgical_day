package com.hospital.surgicalday.controller;

import com.hospital.surgicalday.dto.*;
import com.hospital.surgicalday.service.CaseLifecycleService;
import com.hospital.surgicalday.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Surgical day")
public class SurgicalDayController {

    private final ScheduleService scheduleService;
    private final CaseLifecycleService caseLifecycleService;

    @GetMapping("/cases")
    @Operation(summary = "List cases for a surgery date")
    public List<SurgicalCaseResponse> listCases(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return scheduleService.listByDate(date);
    }

    @PostMapping("/cases")
    @Operation(summary = "Book a surgical case (validates surgeon, patient, theatre, equipment, recovery)")
    public SurgicalCaseResponse book(@Valid @RequestBody BookCaseRequest request) {
        return scheduleService.book(request);
    }

    @PostMapping("/schedules/{date}/publish")
    @Operation(summary = "Validate and publish a surgery schedule")
    public SchedulePublicationResponse publish(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return scheduleService.publish(date);
    }

    @PatchMapping("/cases/{id}/status")
    @Operation(summary = "Advance case status toward discharge")
    public SurgicalCaseResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return caseLifecycleService.updateStatus(id, request);
    }

    @GetMapping("/cases/{id}/bill")
    @Operation(summary = "Get discharge bill for a case")
    public BillResponse getBill(@PathVariable Long id) {
        return caseLifecycleService.getBill(id);
    }

    @GetMapping("/recovery/occupancy")
    @Operation(summary = "Projected recovery bed occupancy at a time")
    public RecoveryOccupancyResponse occupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime at) {
        return scheduleService.occupancy(date, at);
    }
}
