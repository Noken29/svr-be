package com.noken29.svrbe.domain;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fuel_type")
public class FuelType {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "description", length = 48, nullable = false)
    private String description;

    @Expose
    @Column(name = "cost", nullable = false)
    private double cost;
}