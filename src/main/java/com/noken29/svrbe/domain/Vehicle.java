package com.noken29.svrbe.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.annotation.RestResource;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;

    @Column(name = "fuel_consumption")
    private double fuelConsumption;

    @Column(name = "carrying_capacity")
    private double carryingCapacity;

    @Column(name = "volume")
    private double volume;
}
