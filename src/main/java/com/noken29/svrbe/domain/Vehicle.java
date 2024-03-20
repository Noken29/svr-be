package com.noken29.svrbe.domain;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.rest.core.annotation.RestResource;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "description", nullable = false)
    private String description;

    @Expose
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Expose
    @Column(name = "fuel_consumption", nullable = false)
    private double fuelConsumption;

    @Expose
    @Column(name = "carrying_capacity", nullable = false)
    private double carryingCapacity;

    @Expose
    @Column(name = "volume", nullable = false)
    private double volume;
}
