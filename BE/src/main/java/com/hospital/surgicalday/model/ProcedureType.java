package com.hospital.surgicalday.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "procedure_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int defaultMinutes;

    @Column(nullable = false)
    private int recoveryMinutes;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "procedure_required_equipment", joinColumns = @JoinColumn(name = "procedure_id"))
    @Column(name = "equipment")
    @Builder.Default
    private Set<String> requiredEquipment = new HashSet<>();
}
