package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "customer")
public class Customer {
    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "name", length = 96, nullable = false)
    private String name;

    @Expose
    @Column(name = "phone_number", length = 48, nullable = false)
    private String phoneNumber;

    @Expose
    @Column(name = "address_lines", nullable = false)
    private String addressLines;

    @Expose
    @Column(name = "special_requirements", columnDefinition = "TEXT", nullable = false)
    private String specialRequirements;

    @Expose
    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Expose
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

    @Expose
    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @EqualsAndHashCode.Exclude
    private Set<Package> packages;
}