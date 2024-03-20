package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "package")
public class Package {
    @Expose
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
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Expose
    @Column(name = "type", length = 48, nullable = false)
    private String type;

    @Expose
    @Column(name = "weight", nullable = false)
    private double weight;

    @Expose
    @Column(name = "volume", nullable = false)
    private double volume;

    @Expose
    @Column(name = "cost", nullable = false)
    private double cost;
}
