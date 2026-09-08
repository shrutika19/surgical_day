package com.hospital.surgicalday.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "surgical_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgicalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private LocalDate surgeryDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private int durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "procedure_id")
    private ProcedureType procedure;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "surgeon_id")
    private Surgeon surgeon;

    private LocalDateTime recoveryStartedAt;

    private LocalDateTime dischargedAt;

    public LocalDateTime surgeryStartDateTime() {
        return LocalDateTime.of(surgeryDate, startTime);
    }

    public LocalDateTime surgeryEndDateTime() {
        return surgeryStartDateTime().plusMinutes(durationMinutes);
    }

    public LocalDateTime projectedRecoveryStart() {
        return surgeryEndDateTime();
    }

    public LocalDateTime projectedRecoveryEnd() {
        return projectedRecoveryStart().plusMinutes(procedure.getRecoveryMinutes());
    }
}
