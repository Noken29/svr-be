package com.noken29.vrpjobs.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SplitDeliveryVehicleRoutingProblem {
    private final List<VrpVehicle> vehicles;
    private final Graph graph;
    private final int numPackages;
    private double totalWeight;
    private double totalVolume;

    public SplitDeliveryVehicleRoutingProblem(List<VrpVehicle> vehicles,
                                       VrpDepot depot,
                                       List<VrpCustomer> customers,
                                       List<List<Double>> distanceMatrix) {
        this.vehicles = vehicles;
        this.graph = new Graph(depot, customers, distanceMatrix);
        this.numPackages = customers.stream().map(c -> c.getPackages().size()).reduce(0, Integer::sum);
        for (var customer: customers) {
            this.totalWeight += customer.getTotalWeight();
            this.totalVolume += customer.getTotalVolume();
        }
    }
}