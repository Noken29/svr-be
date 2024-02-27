package com.noken29.svrbe.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fuel_type")
public class FuelType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 48, nullable = false)
    private String description;

    @Column(name = "cost", nullable = false)
    private double cost;
}