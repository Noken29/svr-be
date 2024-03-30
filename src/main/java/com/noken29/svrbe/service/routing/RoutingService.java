package com.noken29.svrbe.service.routing;

import com.noken29.svrbe.domain.RoutingSession;
import com.noken29.vrpjobs.model.VrpCustomer;
import com.noken29.vrpjobs.model.VrpDepot;
import com.noken29.vrpjobs.model.VrpPackage;
import com.noken29.vrpjobs.model.SplitDeliveryVehicleRoutingProblem;
import com.noken29.vrpjobs.model.VrpVehicle;
import com.noken29.vrpjobs.solver.aco.AcoContext;
import com.noken29.vrpjobs.solver.aco.AcoSolver;
import com.noken29.vrpjobs.solver.aco.VrpSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class RoutingService {

    @Autowired
    private DistanceCalculator distanceCalculator;

    @Value("#{${aco-solver.v-params}}")
    private Map<String, Double> vParams;

    @Value("#{${aco-solver.c-params}}")
    private Map<String, Double> cParams;

    public SolutionData makeRoutes(RoutingSession routingSession) {
        var acoContext = new AcoContext(
                buildSDVRPInstance(routingSession),
                1.0,
                30,
                0.3,
                new HashMap<>(vParams),
                new HashMap<>(cParams),
                0.5,
                3
        );
        var acoSolver = new AcoSolver(acoContext);
        return buildSolutionsData(routingSession, acoSolver.makeActions(4000, 5, 50));
    }

    private SolutionData buildSolutionsData(RoutingSession routingSession, VrpSolution solution) {
        return SolutionData.builder()
                .depot(routingSession.getDepot())
                .customers(routingSession.getCustomers())
                .vehicles(routingSession.getVehicles())
                .routes(solution.getRoutes().stream().map(
                        r -> Route.builder()
                                .vehicleId(r.getVehicle().getId())
                                .customersIds(r.getCustomers().stream().map(
                                        c -> c.getId()).toList())
                                .packagesIds(r.getPackages().stream().map(
                                        p -> p.getId()).toList())
                                .length(r.getLength())
                                .cost(r.getCost())
                                .build()).toList())
                .totalLength(solution.getTotalLength())
                .totalCost(solution.getTotalCost())
                .build();
    }

    private SplitDeliveryVehicleRoutingProblem buildSDVRPInstance(RoutingSession routingSession) {
        Function<com.noken29.svrbe.domain.Vehicle, Double> calculateServiceCost = (vehicle) ->
                vehicle.getFuelConsumption() / 100 * vehicle.getFuelType().getCost();
        List<VrpVehicle> mappedVehicles = routingSession.getVehicles().stream()
                .map(e -> new VrpVehicle(e.getId(), e.getCarryingCapacity(), e.getVolume(), calculateServiceCost.apply(e)))
                .toList();

        VrpDepot mappedDepot = new VrpDepot(
                routingSession.getDepot().getId(),
                BigDecimal.valueOf(routingSession.getDepot().getLatitude()),
                BigDecimal.valueOf(routingSession.getDepot().getLongitude())
        );

        Function<com.noken29.svrbe.domain.Customer, List<VrpPackage>> mapPackages = (customer) ->
                customer.getPackages().stream()
                        .map(e -> new VrpPackage(e.getId(), e.getWeight(), e.getVolume()))
                        .toList();
        List<VrpCustomer> mappedCustomers = routingSession.getCustomers().stream()
                .map(e -> new VrpCustomer(e.getId(), BigDecimal.valueOf(e.getLatitude()), BigDecimal.valueOf(e.getLongitude()), mapPackages.apply(e)))
                .toList();

        return new SplitDeliveryVehicleRoutingProblem(mappedVehicles, mappedDepot, mappedCustomers, distanceCalculator.buildDistanceMatrix(mappedDepot, mappedCustomers));
    }

}
