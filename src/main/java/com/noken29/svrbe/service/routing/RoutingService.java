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
    private Map<String, Double> V_PARAMS;

    @Value("#{${aco-solver.c-params}}")
    private Map<String, Double> C_PARAMS;

    @Value("#{${aco-solver.init-pheromone}}")
    private double INIT_PHEROMONE = 1;

    @Value("#{${aco-solver.pheromone-evaporation}}")
    private double PHEROMONE_EVAPORATION = 0.3;

    @Value("#{${aco-solver.params-left-bound}}")
    private double PARAMS_LEFT_BOUND = 0.5;

    @Value("#{${aco-solver.params-right-bound}}")
    private double PARAMS_RIGHT_BOUND = 3;

    @Value("#{${aco-solver.probability-of-requesting-new-routes}}")
    private double PROBABILITY_OF_REQUESTING_NEW_ROUTES = 0.5;

    @Value("#{${aco-solver.s-params}}")
    private Map<String, Double> S_PARAMS;

    @Value("#{${aco-solver.num-routes-factor-length}}")
    private double NUM_ROUTES_FACTOR_LENGTH;

    @Value("#{${aco-solver.num-routes-factor-shift}}")
    private double NUM_ROUTES_FACTOR_SHIFT;

    public SolutionData makeRoutes(RoutingSession routingSession) {
        var sdvrpInstance = buildSDVRPInstance(routingSession);
        var solverParameters = getAcoSolverParameters(sdvrpInstance);
        var acoContext = new AcoContext(
                sdvrpInstance,
                INIT_PHEROMONE,
                solverParameters.get("numSolutions"),
                PHEROMONE_EVAPORATION,
                new HashMap<>(V_PARAMS),
                new HashMap<>(C_PARAMS),
                PARAMS_LEFT_BOUND,
                PARAMS_RIGHT_BOUND,
                PROBABILITY_OF_REQUESTING_NEW_ROUTES,
                S_PARAMS,
                NUM_ROUTES_FACTOR_LENGTH,
                NUM_ROUTES_FACTOR_SHIFT
        );
        var acoSolver = new AcoSolver(acoContext);
        return buildSolutionsData(
                routingSession,
                acoSolver.makeActions(
                        routingSession.getId(),
                        solverParameters.get("times"),
                        solverParameters.get("kBest"),
                        solverParameters.get("stagnationThreshold")
                )
        );
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
    private Map<String, Integer> getAcoSolverParameters(SplitDeliveryVehicleRoutingProblem sdvrpInstance) {
        int numCustomers = sdvrpInstance.getGraph().getCustomersIndexes().size();
        int numPackages = sdvrpInstance.getNumPackages();

        int times = 5000 - (numCustomers >= 30 ? 1000 : (numCustomers >= 15 ? 500 : 0)) - (numPackages >= 1000 ? 1000: (numPackages >= 500 ? 500 : 0));
        int numSolutions = 40 - (numCustomers >= 30 ? 10 : (numCustomers >= 15 ? 5 : 0)) - (numPackages >= 1000 ? 10 : (numPackages >= 500 ? 5 : 0));
        int kBest = 15 - (numCustomers >= 30 ? 4 : (numCustomers >= 15 ? 2 : 0));

        Map<String, Integer> params = new HashMap<>();
        params.put("times", times);
        params.put("numSolutions", numSolutions);
        params.put("kBest", kBest);
        params.put("stagnationThreshold", 50);
        return params;
    }

    private final Function<com.noken29.svrbe.domain.Vehicle, Double> calculateServiceCost = (vehicle) ->
            vehicle.getFuelConsumption() / 100 * vehicle.getFuelType().getCost();
    private final Function<com.noken29.svrbe.domain.Customer, List<VrpPackage>> mapPackages = (customer) ->
            customer.getPackages().stream()
                    .map(e -> new VrpPackage(e.getId(), e.getWeight(), e.getVolume() / 1000))
                    .toList();

    private SplitDeliveryVehicleRoutingProblem buildSDVRPInstance(RoutingSession routingSession) {
        List<VrpVehicle> mappedVehicles = routingSession.getVehicles().stream()
                .map(e -> new VrpVehicle(e.getId(), e.getCarryingCapacity(), e.getVolume(), calculateServiceCost.apply(e)))
                .toList();

        VrpDepot mappedDepot = new VrpDepot(
                routingSession.getDepot().getId(),
                BigDecimal.valueOf(routingSession.getDepot().getLatitude()),
                BigDecimal.valueOf(routingSession.getDepot().getLongitude())
        );

        List<VrpCustomer> mappedCustomers = routingSession.getCustomers().stream()
                .map(e -> new VrpCustomer(e.getId(), BigDecimal.valueOf(e.getLatitude()), BigDecimal.valueOf(e.getLongitude()), mapPackages.apply(e)))
                .toList();

        return new SplitDeliveryVehicleRoutingProblem(mappedVehicles, mappedDepot, mappedCustomers, distanceCalculator.buildDistanceMatrix(mappedDepot, mappedCustomers));
    }

}
