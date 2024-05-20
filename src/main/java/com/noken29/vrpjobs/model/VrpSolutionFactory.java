package com.noken29.vrpjobs.model;

import com.noken29.vrpjobs.solver.aco.VrpRoute;
import com.noken29.vrpjobs.solver.aco.VrpSolution;

import java.util.List;
import java.util.Map;

public class VrpSolutionFactory {
    private Map<String, Double> sParams;
    private double numRoutesFactorLength;
    private double numRoutesFactorShift;

    public VrpSolutionFactory(Map<String, Double> sParams, double numRoutesFactorLength, double numRoutesFactorShift) {
        this.sParams = sParams;
        this.numRoutesFactorLength = numRoutesFactorLength;
        this.numRoutesFactorShift = numRoutesFactorShift;
    }

    public VrpSolution build(List<VrpRoute> routes) {
        return new VrpSolution(routes, sParams, numRoutesFactorLength, numRoutesFactorShift);
    }
}
