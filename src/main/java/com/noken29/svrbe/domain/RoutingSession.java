package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    private List<Vehicle> vehicles;

    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "routingSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Customer> customers;

    @EqualsAndHashCode.Exclude
    @OneToOne(
            mappedBy = "routingSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Depot depot;

    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "routingSession",
            cascade = CascadeType.ALL
    )
    private List<Solution> solutions = new ArrayList<>();
}
