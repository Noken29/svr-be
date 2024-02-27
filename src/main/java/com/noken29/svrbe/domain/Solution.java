package com.noken29.svrbe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@Entity
@Table(name = "solution")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routing_session_id", referencedColumnName = "id", nullable = false)
    private RoutingSession routingSession;

    @Column(name = "created", nullable = false)
    private Date created;

    // @Column(name = "routes", length = 256 * 1024 * 1024, nullable = false)
    // @ToString.Exclude
    // @EqualsAndHashCode.Exclude
    // private Json routes;
}
