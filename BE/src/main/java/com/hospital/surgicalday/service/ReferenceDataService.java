package com.hospital.surgicalday.service;

import com.hospital.surgicalday.model.Building;
import com.hospital.surgicalday.model.Patient;
import com.hospital.surgicalday.model.ProcedureType;
import com.hospital.surgicalday.model.RecoveryBed;
import com.hospital.surgicalday.model.Surgeon;
import com.hospital.surgicalday.model.Theatre;
import com.hospital.surgicalday.repository.BuildingRepository;
import com.hospital.surgicalday.repository.PatientRepository;
import com.hospital.surgicalday.repository.ProcedureTypeRepository;
import com.hospital.surgicalday.repository.RecoveryBedRepository;
import com.hospital.surgicalday.repository.SurgeonRepository;
import com.hospital.surgicalday.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceDataService {

    private final BuildingRepository buildingRepository;
    private final TheatreRepository theatreRepository;
    private final SurgeonRepository surgeonRepository;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final PatientRepository patientRepository;
    private final RecoveryBedRepository recoveryBedRepository;

    public List<Building> buildings() {
        return buildingRepository.findAll();
    }

    public List<Theatre> theatres() {
        return theatreRepository.findAll();
    }

    public List<Surgeon> surgeons() {
        return surgeonRepository.findAll();
    }

    public List<ProcedureType> procedures() {
        return procedureTypeRepository.findAll();
    }

    public List<Patient> patients() {
        return patientRepository.findAll();
    }

    public List<RecoveryBed> recoveryBeds() {
        return recoveryBedRepository.findAll();
    }
}
