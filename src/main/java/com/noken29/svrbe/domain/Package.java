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
            referencedColumnName = "id"
    )
    @JsonIgnore
    private Customer customer;

    @Column(name = "type", length = 48)
    private String type;

    @Column(name = "weight")
    private double weight;

    @Column(name = "volume")
    private double volume;

    @Column(name = "cost")
    private double cost;
}
