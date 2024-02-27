package com.noken29.svrbe.domain.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutingSessionInfo {
    private Long id;
    private String description;
    private int numberOfSolutions;
    private Date lastSaved;
}
