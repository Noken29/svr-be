package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "package")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @ToString.Exclude
    @JoinColumn(
            name = "customer_id",
            referencedColumnName = "id",
            nullable = false
    )
    @JsonIgnore
    private Customer customer;

    @Column(name = "type", length = 48, nullable = false)
    private String type;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "volume", nullable = false)
    private double volume;

    @Column(name = "cost", nullable = false)
    private double cost;
}
