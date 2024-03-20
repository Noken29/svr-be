package com.noken29.vrpjobs.solver.aco;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class VrpSolution {
    private final List<VrpRoute> routes;
    private final double totalLength;
    private final double totalCost;

    public VrpSolution(List<VrpRoute> routes) {
        this.routes = routes;
        this.totalLength = calculateTotalLength();
        this.totalCost = calculateTotalCost();
    }
    private double calculateTotalLength() {
        return routes.stream().map(VrpRoute::getLength).reduce(0.0, Double::sum);
    }

    private double calculateTotalCost() {
        return routes.stream().map(VrpRoute::getCost).reduce(0.0, Double::sum);
    }

    public int getNumRoutes() {
        return routes.size();
    }
}
