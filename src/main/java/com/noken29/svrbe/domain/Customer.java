package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 96, nullable = false)
    private String name;

    @Column(name = "phone_number", length = 48, nullable = false)
    private String phoneNumber;

    @Column(name = "address_lines", nullable = false)
    private String addressLines;

    @Column(name = "special_requirements", columnDefinition = "TEXT", nullable = false)
    private String specialRequirements;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "routing_session_id",
            referencedColumnName = "id",
            nullable = false
    )
    private RoutingSession routingSession;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Package> packages;
}