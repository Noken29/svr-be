package com.noken29.vrpjobs.solver.aco;

import com.noken29.vrpjobs.utils.MathUtils;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class VrpSolution {
    private final List<VrpRoute> routes;
    private final double totalLength;
    private final double totalCost;
    private final double fitness;

    public VrpSolution(List<VrpRoute> routes, Map<String, Double> sParams, double numRoutesFactorLength, double numRoutesFactorHeight) {
        this.routes = routes;
        this.totalLength = calculateTotalLength();
        this.totalCost = calculateTotalCost();

        this.fitness = this.calculateTotalFitness(sParams, numRoutesFactorLength, numRoutesFactorHeight);
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

    public double calculateTotalFitness(Map<String, Double> sParams, double numRoutesFactorLength, double numRoutesFactorHeight) {
        double numRoutes = getNumRoutes();
        return Math.pow(MathUtils.sigmoid(numRoutes, numRoutesFactorLength, numRoutesFactorHeight), sParams.get("numRoutes"))
                * Math.pow(calculateVehiclesFitness(), sParams.get("serviceCost"))
                * Math.pow(calculateCustomersFitness(), sParams.get("distance"));
    }

    public double calculateVehiclesFitness() {
        return totalCost;
    }

    public double calculateCustomersFitness() {
        return totalLength;
    }
}
