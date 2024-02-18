package com.noken29.svrbe.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "solution")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routing_session_id", referencedColumnName = "id")
    private RoutingSession routingSession;

    @Column(name = "created")
    private Date created;

//    @Column(name = "routes", length = 256 * 1024 * 1024)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Json routes;
}
