package com.noken29.svrbe.domain.view;

import com.noken29.svrbe.domain.Customer;
import com.noken29.svrbe.domain.Depot;
import com.noken29.svrbe.domain.Solution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutingSessionView {
    private Long id;
    private String description;
    private Set<Long> vehicleIds;
    private Set<Customer> customers;
    private Depot depot;
    private Set<Solution> solutions;
}
