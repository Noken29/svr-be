package com.noken29.vrpjobs.solver.aco;

import com.noken29.svrbe.domain.exception.RoutingException;
import com.noken29.vrpjobs.model.VrpCustomer;
import com.noken29.vrpjobs.model.VrpPackage;
import com.noken29.vrpjobs.model.VrpVehicle;
import com.noken29.vrpjobs.utils.ComplexKey;
import com.noken29.vrpjobs.utils.DebugUtils;

import java.util.*;

public class AcoSolver {
    private final AcoContext context;

    public AcoSolver(AcoContext context) {
        this.context = context;
    }

    public VrpSolution makeActions(Long routingSessionId, int times, int kBest, int stagnationThreshold) {
        Map<VrpCustomer, Set<VrpVehicle>> bannedVehicles = new HashMap<>();
        context.getProblem().getGraph().getCustomersIndexes().forEach(e ->
                bannedVehicles.put(context.getProblem().getGraph().getCustomer(e), new HashSet<>()));

        VrpSolution globallyOptimalSolution = generateSolution(bannedVehicles);
        int stagnationFactor = 0;

        List<List<String>> statistics = new ArrayList<>(times + 1);

        for (int i = 0; i <= times; i++) {
            List<VrpSolution> solutions = new ArrayList<>(context.getNumSolutions() + 1);
            for (int j = 0; j < context.getNumSolutions(); j++) {
                solutions.add(generateSolution(bannedVehicles));
            }
            solutions.sort(Comparator.comparing(VrpSolution::getFitness));
            calculateScoresAndUpdatePheromone(solutions.subList(0, kBest));
            VrpSolution locallyOptimalSolution = solutions.get(0);
            if (locallyOptimalSolution.getFitness() < globallyOptimalSolution.getFitness()) {
                globallyOptimalSolution = locallyOptimalSolution;
                stagnationFactor = 0;
            } else if (stagnationThreshold != -1) {
                stagnationFactor++;
            }
            if (stagnationFactor == stagnationThreshold) {
                context.updateParams(globallyOptimalSolution, locallyOptimalSolution);
                stagnationFactor = 0;
            }
            statistics.add(List.of(
                    String.valueOf(i),
                    String.valueOf(locallyOptimalSolution.getNumRoutes()),
                    String.valueOf(locallyOptimalSolution.getTotalCost()),
                    String.valueOf(locallyOptimalSolution.getTotalLength()),
                    String.valueOf(locallyOptimalSolution.getFitness())
            ));
        }
        DebugUtils.writeToCSVFile("RSID_" + routingSessionId + "_", statistics);
        return globallyOptimalSolution;
    }

    public VrpSolution generateSolution(Map<VrpCustomer, Set<VrpVehicle>> bannedVehicles) {
        List<VrpRoute> routes = new LinkedList<>();
        double totalWeight = context.getProblem().getTotalWeight();
        double totalVolume = context.getProblem().getTotalVolume();

        Set<VrpCustomer> deliveredCustomers = new HashSet<>();
        Set<VrpPackage> deliveredPackages = new HashSet<>();

        boolean requestingNewRoutesIsPossible = Math.random() <= context.getProbabilityOfRequestingNewRoutes();

        while (deliveredCustomers.size() != context.getProblem().getGraph().getCustomersIndexes().size()) {
            List<VrpCustomer> routeCustomers = new LinkedList<>();
            List<VrpPackage> routePackages = new LinkedList<>();

            VrpCustomer routePrevCustomer = context.chooseFirstCustomer(deliveredCustomers);
            double routeLength = context.getProblem().getGraph().getDistanceFromDepot(routePrevCustomer.getIndex());
            double routePickedWeight = 0.0;
            double routePickedVolume = 0.0;

            if (bannedVehicles.get(routePrevCustomer).size() == context.getProblem().getVehicles().size())
                throw new RoutingException("Unable to deliver one from clients.", routePrevCustomer.getId());

            VrpVehicle routeVehicle = context.chooseVehicle(routePrevCustomer, totalWeight, totalVolume, bannedVehicles.get(routePrevCustomer));

            routeCustomers.add(routePrevCustomer);
            boolean vehicleFilled = false;

            AcoContext.PackageChoosingStrategy pcs = context.choosePackageChoosingStrategy();
            List<VrpPackage> customerPackages = pcs == AcoContext.PackageChoosingStrategy.WEIGHT ?
                    routePrevCustomer.getPackagesSortedWeight() : routePrevCustomer.getPackagesSortedVolume();

            boolean atLeastOnePackageAdded = false;

            for (VrpPackage p : customerPackages) {
                if (deliveredPackages.contains(p))
                    continue;
                if (routePickedWeight + p.getWeight() <= routeVehicle.getMaxWeight()
                        && routePickedVolume + p.getVolume() <= routeVehicle.getMaxVolume()) {
                    routePickedWeight += p.getWeight();
                    routePickedVolume += p.getVolume();
                    routePackages.add(p);
                    deliveredPackages.add(p);
                    atLeastOnePackageAdded = true;
                } else
                    vehicleFilled = true;
            }

            if (!atLeastOnePackageAdded) {
                bannedVehicles.get(routePrevCustomer).add(routeVehicle);
                continue;
            }

            if (!vehicleFilled)
                deliveredCustomers.add(routePrevCustomer);

            if (requestingNewRoutesIsPossible
                    && context.requestNewRoute(routeLength, routes.size(), deliveredCustomers.size(), context.getProblem().getGraph().getCustomersIndexes().size())) {
                routes.add(new VrpRoute(
                        routeVehicle,
                        routeCustomers,
                        routePackages,
                        pcs,
                        routePickedWeight,
                        routePickedVolume,
                        routeLength
                ));
                continue;
            }

            while (!vehicleFilled && deliveredCustomers.size() != context.getProblem().getGraph().getCustomersIndexes().size()) {
                VrpCustomer routeNextCustomer = context.chooseCustomer(deliveredCustomers, routePrevCustomer, routeVehicle);
                customerPackages = pcs == AcoContext.PackageChoosingStrategy.WEIGHT ?
                        routeNextCustomer.getPackagesSortedWeight() : routeNextCustomer.getPackagesSortedVolume();

                atLeastOnePackageAdded = false;
                for (VrpPackage p : customerPackages) {
                    if (deliveredPackages.contains(p))
                        continue;
                    if (routePickedWeight + p.getWeight() <= routeVehicle.getMaxWeight()
                            && routePickedVolume + p.getVolume() <= routeVehicle.getMaxVolume()) {
                        routePickedWeight += p.getWeight();
                        routePickedVolume += p.getVolume();
                        routePackages.add(p);
                        deliveredPackages.add(p);
                        atLeastOnePackageAdded = true;
                    } else
                        vehicleFilled = true;
                }

                if (atLeastOnePackageAdded) {
                    routeLength += context.getProblem().getGraph().getDistanceBetween(routePrevCustomer.getIndex(), routeNextCustomer.getIndex());
                    routeCustomers.add(routeNextCustomer);
                }

                if (!vehicleFilled)
                    deliveredCustomers.add(routeNextCustomer);
                routePrevCustomer = routeNextCustomer;
            }

            routeLength += context.getProblem().getGraph().getDistanceFromDepot(routeCustomers.get(routeCustomers.size() - 1).getIndex());
            totalWeight -= routePickedWeight;
            totalVolume -= routePickedVolume;

            routes.add(new VrpRoute(
                    routeVehicle,
                    routeCustomers,
                    routePackages,
                    pcs,
                    routePickedWeight,
                    routePickedVolume,
                    routeLength
            ));
        }

        return context.getVrpSolutionFactory().build(routes);
    }

    private void calculateScoresAndUpdatePheromone(List<VrpSolution> solutions) {
        Map<ComplexKey, Double> vehicleCustomerScores = new HashMap<>();
        Map<ComplexKey, Double> nodeNodeScores = new HashMap<>();
        Map<AcoContext.PackageChoosingStrategy, Double> pcsScores = new HashMap<>();

        pcsScores.put(AcoContext.PackageChoosingStrategy.WEIGHT, 0.0);
        pcsScores.put(AcoContext.PackageChoosingStrategy.VOLUME, 0.0);

        for (VrpSolution s : solutions) {
            for (VrpRoute r : s.getRoutes()) {
                ComplexKey vcKey = ComplexKey.from(r.getVehicle().getIndex(), r.getCustomers().get(0).getIndex());
                vehicleCustomerScores.put(vcKey, vehicleCustomerScores.getOrDefault(vcKey, 0.0) + (1.0 / r.getVehicle().getServiceCost()));

                ComplexKey dcKey = ComplexKey.from(context.getProblem().getGraph().getDepotIndex(), r.getCustomers().get(0).getIndex());
                nodeNodeScores.put(dcKey, 1.0 / context.getProblem().getGraph().getDistanceFromDepot(r.getCustomers().get(0).getIndex()));

                if (r.getCustomers().size() > 1) {
                    for (int i = 1; i < r.getCustomers().size(); i++) {
                        ComplexKey ccKey = ComplexKey.from(r.getCustomers().get(i - 1).getIndex(), r.getCustomers().get(i).getIndex());
                        nodeNodeScores.put(ccKey, nodeNodeScores.getOrDefault(ccKey, 0.0)
                                + (1.0 / context.getProblem().getGraph().getDistanceBetween(r.getCustomers().get(i - 1).getIndex(), r.getCustomers().get(i).getIndex())));
                    }
                }
                double pcsScore = (
                        r.getPcs() == AcoContext.PackageChoosingStrategy.WEIGHT ?
                        r.getTotalWeight() / r.getTotalVolume() / this.context.getProblem().getTotalWeight() :
                        r.getTotalVolume() / r.getTotalWeight() / this.context.getProblem().getTotalVolume()
                ) * r.getPackages().size();
                if (Double.isNaN(pcsScore))
                    pcsScore = 0.0;
                pcsScores.put(r.getPcs(), pcsScores.getOrDefault(r.getPcs(), 0.0) + pcsScore);
            }
        }
        context.updatePheromone(vehicleCustomerScores, nodeNodeScores, pcsScores);
    }
}
