package com.noken29.svrbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "solution")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "routing_session_id", referencedColumnName = "id", nullable = false)
    private RoutingSession routingSession;

    @Column(name = "created", nullable = false)
    private Date created;

    @Column(name = "data", length = 256 * 2048 * 2048, nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String data;
}
