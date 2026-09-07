package com.hospital.surgicalday.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "surgeons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surgeon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty;
}
