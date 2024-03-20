package com.noken29.svrbe.service.routing;

import com.google.gson.annotations.Expose;
import com.noken29.svrbe.domain.Customer;
import com.noken29.svrbe.domain.Depot;
import com.noken29.svrbe.domain.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionData {
    @Expose
    private Depot depot;
    @Expose
    private Set<Customer> customers;
    @Expose
    private Set<Vehicle> vehicles;
    @Expose
    private List<Route> routes;
    @Expose
    private double totalLength;
    @Expose
    private double totalCost;
}
