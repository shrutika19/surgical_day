package com.hospital.surgicalday.config;

import com.hospital.surgicalday.model.*;
import com.hospital.surgicalday.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BuildingRepository buildingRepository;
    private final TheatreRepository theatreRepository;
    private final SurgeonRepository surgeonRepository;
    private final RecoveryBedRepository recoveryBedRepository;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final PatientRepository patientRepository;

    @Override
    public void run(String... args) {
        if (buildingRepository.count() > 0) {
            return;
        }

        Building north = buildingRepository.save(Building.builder().name("North Wing").build());
        Building south = buildingRepository.save(Building.builder().name("South Wing").build());

        theatreRepository.save(Theatre.builder()
                .name("OT-1")
                .building(north)
                .equipment(Set.of("LAPAROSCOPE", "GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());
        theatreRepository.save(Theatre.builder()
                .name("OT-2")
                .building(north)
                .equipment(Set.of("ORTHO_DRILL", "C_ARM", "GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());
        theatreRepository.save(Theatre.builder()
                .name("OT-3")
                .building(south)
                .equipment(Set.of("MICROSCOPE", "GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());
        theatreRepository.save(Theatre.builder()
                .name("OT-4")
                .building(south)
                .equipment(Set.of("GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());

        surgeonRepository.save(Surgeon.builder().name("Dr. Anita Rao").specialty("General").build());
        surgeonRepository.save(Surgeon.builder().name("Dr. James Cole").specialty("Orthopaedics").build());
        surgeonRepository.save(Surgeon.builder().name("Dr. Priya Shah").specialty("ENT").build());
        surgeonRepository.save(Surgeon.builder().name("Dr. Omar Hassan").specialty("General").build());

        // Three recovery beds — enough to demo overflow when four patients land at once
        recoveryBedRepository.save(RecoveryBed.builder().label("REC-A1").building(north).build());
        recoveryBedRepository.save(RecoveryBed.builder().label("REC-A2").building(north).build());
        recoveryBedRepository.save(RecoveryBed.builder().label("REC-B1").building(south).build());

        procedureTypeRepository.save(ProcedureType.builder()
                .name("Laparoscopic Cholecystectomy")
                .defaultMinutes(90)
                .recoveryMinutes(60)
                .basePrice(new BigDecimal("3200.00"))
                .requiredEquipment(Set.of("LAPAROSCOPE", "GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());
        procedureTypeRepository.save(ProcedureType.builder()
                .name("Knee Arthroscopy")
                .defaultMinutes(75)
                .recoveryMinutes(45)
                .basePrice(new BigDecimal("2800.00"))
                .requiredEquipment(Set.of("ORTHO_DRILL", "C_ARM", "GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());
        procedureTypeRepository.save(ProcedureType.builder()
                .name("Tympanoplasty")
                .defaultMinutes(120)
                .recoveryMinutes(60)
                .basePrice(new BigDecimal("3500.00"))
                .requiredEquipment(Set.of("MICROSCOPE", "GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());
        procedureTypeRepository.save(ProcedureType.builder()
                .name("Hernia Repair")
                .defaultMinutes(60)
                .recoveryMinutes(45)
                .basePrice(new BigDecimal("2100.00"))
                .requiredEquipment(Set.of("GENERAL_TRAY", "ANESTHESIA_MACHINE"))
                .build());

        patientRepository.save(Patient.builder().fullName("Alice Mercer").medicalRecordNumber("MRN-1001").build());
        patientRepository.save(Patient.builder().fullName("Ben Ortiz").medicalRecordNumber("MRN-1002").build());
        patientRepository.save(Patient.builder().fullName("Chloe Nguyen").medicalRecordNumber("MRN-1003").build());
        patientRepository.save(Patient.builder().fullName("David Patel").medicalRecordNumber("MRN-1004").build());
        patientRepository.save(Patient.builder().fullName("Elena Brooks").medicalRecordNumber("MRN-1005").build());
        patientRepository.save(Patient.builder().fullName("Farid Khan").medicalRecordNumber("MRN-1006").build());
    }
}
