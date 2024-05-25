package com.noken29.vrpjobs.solver.aco;

import com.noken29.vrpjobs.model.VrpCustomer;
import com.noken29.vrpjobs.model.SplitDeliveryVehicleRoutingProblem;
import com.noken29.vrpjobs.model.VrpSolutionFactory;
import com.noken29.vrpjobs.model.VrpVehicle;
import com.noken29.vrpjobs.utils.ComplexKey;
import com.noken29.vrpjobs.utils.MathUtils;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Getter
public class AcoContext {
    private final SplitDeliveryVehicleRoutingProblem problem;
    private final int numSolutions;
    private final double pheromoneEvaporation;
    private final Map<String, Double> vParams;
    private final Map<String, Double> cParams;
    private final double paramsLeftBound;
    private final double paramsRightBound;
    private final double probabilityOfRequestingNewRoutes;
    private final Map<String, Double> sParams;
    private final double numRoutesFactorLength;
    private final double numRoutesFactorShift;

    private final Map<ComplexKey, Double> vehicleCustomerPheromone = new HashMap<>();
    private final Map<ComplexKey, Double> nodeNodePheromone = new HashMap<>();
    private final Map<PackageChoosingStrategy, Double> packageChoosingStrategyPheromone = new HashMap<>();
    private final VrpSolutionFactory vrpSolutionFactory;

    public AcoContext(SplitDeliveryVehicleRoutingProblem problem,
                      double initPheromone,
                      int numSolutions,
                      double pheromoneEvaporation,
                      Map<String, Double> vParams,
                      Map<String, Double> cParams,
                      double paramsLeftBound,
                      double paramsRightBound,
                      double probabilityOfRequestingNewRoutes,
                      Map<String, Double> sParams,
                      double numRoutesFactorLength,
                      double numRoutesFactorShift) {
        this.problem = problem;
        this.numSolutions = numSolutions;
        this.pheromoneEvaporation = pheromoneEvaporation;
        this.vParams = vParams;
        this.cParams = cParams;
        this.paramsLeftBound = paramsLeftBound;
        this.paramsRightBound = paramsRightBound;
        this.probabilityOfRequestingNewRoutes = probabilityOfRequestingNewRoutes;
        this.sParams = sParams;
        this.numRoutesFactorLength = numRoutesFactorLength;
        this.numRoutesFactorShift = numRoutesFactorShift;

        fillPheromone(initPheromone);
        this.vrpSolutionFactory = new VrpSolutionFactory(sParams, numRoutesFactorLength, numRoutesFactorShift);
    }

    private void fillPheromone(double initPheromone) {
        for (String vIndex : problem.getVehicles().stream().map(VrpVehicle::getIndex).toList()) {
            for (String cIndex : problem.getGraph().getCustomersIndexes()) {
                vehicleCustomerPheromone.put(ComplexKey.from(vIndex, cIndex), initPheromone);
            }
        }
        for (String cIndex : problem.getGraph().getCustomersIndexes()) {
            nodeNodePheromone.put(ComplexKey.from(problem.getGraph().getDepotIndex(), cIndex), initPheromone);
        }
        for (int i = 0; i < problem.getGraph().getCustomersIndexes().size(); i++) {
            for (int j = 0; j < problem.getGraph().getCustomersIndexes().size(); j++) {
                if (i != j) {
                    String c1Index = problem.getGraph().getCustomersIndexes().get(i);
                    String c2Index = problem.getGraph().getCustomersIndexes().get(j);
                    nodeNodePheromone.put(ComplexKey.from(c1Index, c2Index), initPheromone);
                }
            }
        }
        packageChoosingStrategyPheromone.put(PackageChoosingStrategy.WEIGHT, initPheromone);
        packageChoosingStrategyPheromone.put(PackageChoosingStrategy.VOLUME, initPheromone);
    }

    public VrpCustomer chooseFirstCustomer(Set<VrpCustomer> deliveredCustomers) {
        String depotIndex = problem.getGraph().getDepotIndex();

        List<BigDecimal> weights = new ArrayList<>();
        for (String cIndex : problem.getGraph().getCustomersIndexes()) {
            if (deliveredCustomers.contains(problem.getGraph().getCustomer(cIndex)))
                weights.add(BigDecimal.ZERO);
            else {
                weights.add(BigDecimal.valueOf(
                        Math.pow(nodeNodePheromone.get(ComplexKey.from(depotIndex, cIndex)), cParams.get("pheromone"))
                                * Math.pow(1.0 / problem.getGraph().getDistanceBetween(depotIndex, cIndex), cParams.get("distance"))
                ));
            }
        }

        return problem.getGraph().getCustomer(problem.getGraph().getCustomersIndexes().get(MathUtils.discreteDistribution(weights, weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add))));
    }

    public VrpVehicle chooseVehicle(VrpCustomer firstCustomer, double totalWeight, double totalVolume, Set<VrpVehicle> bannedVehicles) {
        List<BigDecimal> weights = new ArrayList<>();
        for (VrpVehicle vehicle : problem.getVehicles()) {
            if (bannedVehicles.contains(vehicle)) {
                weights.add(BigDecimal.ZERO);
                continue;
            }
            weights.add(BigDecimal.valueOf(Math.pow(vehicleCustomerPheromone.get(ComplexKey.from(vehicle.getIndex(), firstCustomer.getIndex())), vParams.get("pheromone"))
                            * Math.pow(1 / vehicle.getServiceCost(), vParams.get("serviceCost"))
                            * Math.pow(1 / Math.max(firstCustomer.getTotalWeight() / vehicle.getMaxWeight(), firstCustomer.getTotalVolume() / vehicle.getMaxVolume()), vParams.get("customerDemand"))
                            * Math.pow(1 / Math.max(totalWeight / vehicle.getMaxWeight(), totalVolume / vehicle.getMaxVolume()), vParams.get("totalDemand"))
            ));
        }

        return problem.getVehicles().get(MathUtils.discreteDistribution(weights, weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add)));
    }

    public PackageChoosingStrategy choosePackageChoosingStrategy() {
        List<BigDecimal> weights = Arrays.asList(
                BigDecimal.valueOf(packageChoosingStrategyPheromone.get(PackageChoosingStrategy.WEIGHT)),
                BigDecimal.valueOf(packageChoosingStrategyPheromone.get(PackageChoosingStrategy.VOLUME))
        );
        return PackageChoosingStrategy.values()[MathUtils.discreteDistribution(weights, weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add))];
    }

    public boolean requestNewRoute(double routeLength, int numRoutes, int deliveredCustomers, int numCustomers) {
        double routeLengthFactor = MathUtils.sigmoid(routeLength, 80, 5);
        double numRoutesFactor = MathUtils.sigmoid(numRoutes, 2, 5);
        double numCustomersFactor = deliveredCustomers / (double) numCustomers;

        double randomValue = Math.random();
        return randomValue <= routeLengthFactor && (randomValue >= numRoutesFactor || randomValue >= numCustomersFactor);
    }

    public VrpCustomer chooseCustomer(Set<VrpCustomer> deliveredCustomers, VrpCustomer lastSelectedCustomer, VrpVehicle selectedVehicle) {
        List<BigDecimal> weights = new ArrayList<>();
        for (String cIndex: problem.getGraph().getCustomersIndexes()) {
            VrpCustomer c = problem.getGraph().getCustomer(cIndex);
            if (deliveredCustomers.contains(c)) {
                weights.add(BigDecimal.ZERO);
            } else {
                weights.add(BigDecimal.valueOf(
                        Math.pow(nodeNodePheromone.get(ComplexKey.from(lastSelectedCustomer.getIndex(), cIndex)), cParams.get("pheromone"))
                                * Math.pow(1 / problem.getGraph().getDistanceBetween(lastSelectedCustomer.getIndex(), cIndex), cParams.get("distance"))
                                * Math.pow(1 / Math.max(c.getTotalWeight() / selectedVehicle.getMaxWeight(), c.getTotalVolume() / selectedVehicle.getMaxVolume()), cParams.get("demand"))
                ));
            }
        }

        return problem.getGraph().getCustomer(problem.getGraph().getCustomersIndexes().get(MathUtils.discreteDistribution(weights, weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add))));
    }

    public void updatePheromone(Map<ComplexKey, Double> vehicleCustomerScores,
                                Map<ComplexKey, Double> nodeNodeScores,
                                Map<PackageChoosingStrategy, Double> pcsScores) {
        for (var entry : vehicleCustomerScores.entrySet()) {
            vehicleCustomerPheromone.compute(entry.getKey(), (k, v) -> (v == null ? 1.0 : v) * (1.0 - pheromoneEvaporation) + entry.getValue());
        }

        for (var entry : nodeNodeScores.entrySet()) {
            nodeNodePheromone.compute(entry.getKey(), (k, v) -> (v == null ? 1.0 : v) * (1.0 - pheromoneEvaporation) + entry.getValue());
        }

        for (var entry : pcsScores.entrySet()) {
            packageChoosingStrategyPheromone.compute(entry.getKey(), (k, v) -> (v == null ? 1.0 : v) * (1.0 - pheromoneEvaporation) + entry.getValue());
        }
    }

    private double keepValueInBounds(double value) {
        return BigDecimal.valueOf(Math.max(paramsLeftBound, Math.min(value, paramsRightBound))).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public void updateParams(VrpSolution globalOptimalSolution, VrpSolution localOptimalSolution) {
        double vcDiff = localOptimalSolution.calculateVehiclesFitness() / globalOptimalSolution.calculateVehiclesFitness();
        for (var entry : vParams.entrySet()) {
            String key = entry.getKey();
            Double paramValue = entry.getValue();

            int updateStrategy = MathUtils.discreteDistribution(Arrays.asList(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.7)), BigDecimal.ONE);
            switch (updateStrategy) {
                case 0:
                    vParams.put(key, keepValueInBounds(paramValue + Math.random() - 0.5));
                    break;
                case 1:
                    vParams.put(key, keepValueInBounds(paramValue + (Math.random() <= 0.5 ? 1 : -1) * vcDiff));
                    break;
                case 2:
                    vParams.put(key, keepValueInBounds((paramsRightBound - paramsLeftBound) * Math.random() + paramsLeftBound));
                    break;
            }
        }

        double ccDiff = localOptimalSolution.calculateCustomersFitness() / globalOptimalSolution.calculateCustomersFitness();
        for (var entry : cParams.entrySet()) {
            String key = entry.getKey();
            double paramValue = entry.getValue();

            int updateStrategy = MathUtils.discreteDistribution(Arrays.asList(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.7)), BigDecimal.ONE);
            switch (updateStrategy) {
                case 0:
                    cParams.put(key, keepValueInBounds(paramValue + Math.random() - 0.5));
                    break;
                case 1:
                    cParams.put(key, keepValueInBounds(paramValue + (Math.random() <= 0.5 ? 1 : -1) * (ccDiff)));
                    break;
                case 2:
                    cParams.put(key, keepValueInBounds((paramsRightBound - paramsLeftBound) * Math.random() + paramsLeftBound));
                    break;
            }
        }
    }

    public enum PackageChoosingStrategy {
        WEIGHT, VOLUME
    }
}
