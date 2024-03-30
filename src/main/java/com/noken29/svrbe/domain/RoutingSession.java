package com.noken29.svrbe.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "routing_session")
public class RoutingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 96)
    private String description;

    @Column(name = "last_saved")
    private Date lastSaved;

    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "vehicle_routing_session",
            joinColumns = @JoinColumn(name = "routing_session_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private Set<Vehicle> vehicles;

    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "routingSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Customer> customers;

    @EqualsAndHashCode.Exclude
    @OneToOne(
            mappedBy = "routingSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Depot depot;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "routingSession",
            cascade = CascadeType.ALL
    )
    private Set<Solution> solutions = new HashSet<>();

    @Formula("(SELECT IF(COUNT(s.id) = 0, FALSE, TRUE) FROM routing_session rs JOIN solution s ON s.routing_session_id = rs.id WHERE rs.id = id)")
    private boolean haveSolutions;
}
