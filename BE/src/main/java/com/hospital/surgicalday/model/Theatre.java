package com.hospital.surgicalday.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "theatres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id")
    private Building building;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "theatre_equipment", joinColumns = @JoinColumn(name = "theatre_id"))
    @Column(name = "equipment")
    @Builder.Default
    private Set<String> equipment = new HashSet<>();
}
