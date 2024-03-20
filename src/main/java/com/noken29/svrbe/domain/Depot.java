package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "depot")
public class Depot {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "address_lines", nullable = false)
    private String addressLines;

    @Expose
    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Expose
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "routing_session_id",
            referencedColumnName = "id",
            nullable = false
    )
    private RoutingSession routingSession;
}
