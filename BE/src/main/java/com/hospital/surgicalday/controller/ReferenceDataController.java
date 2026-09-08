package com.hospital.surgicalday.controller;

import com.hospital.surgicalday.model.*;
import com.hospital.surgicalday.service.ReferenceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reference data")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    @GetMapping("/buildings")
    @Operation(summary = "List buildings")
    public List<Building> buildings() {
        return referenceDataService.buildings();
    }

    @GetMapping("/theatres")
    @Operation(summary = "List theatres with equipment")
    public List<Theatre> theatres() {
        return referenceDataService.theatres();
    }

    @GetMapping("/surgeons")
    @Operation(summary = "List surgeons")
    public List<Surgeon> surgeons() {
        return referenceDataService.surgeons();
    }

    @GetMapping("/procedures")
    @Operation(summary = "List procedure types")
    public List<ProcedureType> procedures() {
        return referenceDataService.procedures();
    }

    @GetMapping("/patients")
    @Operation(summary = "List patients")
    public List<Patient> patients() {
        return referenceDataService.patients();
    }

    @GetMapping("/recovery-beds")
    @Operation(summary = "List recovery beds")
    public List<RecoveryBed> recoveryBeds() {
        return referenceDataService.recoveryBeds();
    }
}
