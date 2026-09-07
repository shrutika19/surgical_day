package com.hospital.surgicalday.controller;

import com.hospital.surgicalday.model.*;
import com.hospital.surgicalday.repository.*;
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

    private final BuildingRepository buildingRepository;
    private final TheatreRepository theatreRepository;
    private final SurgeonRepository surgeonRepository;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final PatientRepository patientRepository;
    private final RecoveryBedRepository recoveryBedRepository;

    @GetMapping("/buildings")
    @Operation(summary = "List buildings")
    public List<Building> buildings() {
        return buildingRepository.findAll();
    }

    @GetMapping("/theatres")
    @Operation(summary = "List theatres with equipment")
    public List<Theatre> theatres() {
        return theatreRepository.findAll();
    }

    @GetMapping("/surgeons")
    @Operation(summary = "List surgeons")
    public List<Surgeon> surgeons() {
        return surgeonRepository.findAll();
    }

    @GetMapping("/procedures")
    @Operation(summary = "List procedure types")
    public List<ProcedureType> procedures() {
        return procedureTypeRepository.findAll();
    }

    @GetMapping("/patients")
    @Operation(summary = "List patients")
    public List<Patient> patients() {
        return patientRepository.findAll();
    }

    @GetMapping("/recovery-beds")
    @Operation(summary = "List recovery beds")
    public List<RecoveryBed> recoveryBeds() {
        return recoveryBedRepository.findAll();
    }
}
