package com.noken29.svrbe.service;

import com.google.gson.GsonBuilder;
import com.noken29.svrbe.api.RoutingSessionAPI;
import com.noken29.svrbe.domain.*;
import com.noken29.svrbe.domain.Package;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;
import com.noken29.svrbe.domain.view.RoutingSessionInfo;
import com.noken29.svrbe.domain.view.RoutingSessionView;
import com.noken29.svrbe.repository.RoutingSessionRepository;
import com.noken29.svrbe.repository.SolutionRepository;
import com.noken29.svrbe.repository.VehicleRepository;
import com.noken29.svrbe.service.routing.RoutingService;
import com.noken29.svrbe.service.routing.SolutionData;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoutingSessionAPIImpl implements RoutingSessionAPI {

    @Autowired
    private RoutingSessionRepository routingSessionRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private RoutingService routingService;

    private final Map<Long, Future<SolutionData>> activeJobs = new HashMap<>();

    @Override
    public RoutingSession getById(Long id) {
        return routingSessionRepository.getReferenceById(id);
    }

    @Override
    public RoutingSessionView getViewById(Long id) {
        return buildRoutingSessionView(getById(id));
    }

    @Override
    @Transactional
    public RoutingSessionView create(RoutingSessionBean bean) {
        RoutingSession routingSession = buildRoutingSession(null, bean);
        routingSession.getDepot().setRoutingSession(routingSession);
        routingSession.getCustomers().forEach(c -> {
            c.setRoutingSession(routingSession);
            c.getPackages().forEach(p -> p.setCustomer(c));
        });
        routingSession.setVehicles(new HashSet<>(vehicleRepository.findAllById(bean.getVehicleIds())));
        return buildRoutingSessionView(routingSessionRepository.save(routingSession));
    }

    @Override
    @Transactional
    public RoutingSessionView update(Long routingSessionId, RoutingSessionBean bean) {
        RoutingSession routingSession = buildRoutingSession(routingSessionId, bean);
        routingSession.getDepot().setRoutingSession(routingSession);
        routingSession.getCustomers().forEach(c -> {
            c.setRoutingSession(routingSession);
            c.getPackages().forEach(p -> p.setCustomer(c));
        });
        routingSession.setVehicles(new HashSet<>(vehicleRepository.findAllById(bean.getVehicleIds())));
        routingSessionRepository.save(routingSession);
        return buildRoutingSessionView(routingSession);
    }

    @Override
    public List<RoutingSessionInfo> getInfo() {
        return routingSessionRepository.findAll().stream().map(
              e -> RoutingSessionInfo.builder()
                      .id(e.getId())
                      .description(e.getDescription())
                      .numberOfSolutions(e.getSolutions().size())
                      .lastSaved(e.getLastSaved())
                      .build()
        ).collect(Collectors.toList());
    }

    private RoutingSession buildRoutingSession(Long routingSessionId, RoutingSessionBean bean) {
        return RoutingSession.builder()
                .id(routingSessionId)
                .description(bean.getDescription())
                .lastSaved(new Date())
                .depot(Depot.builder()
                        .id(bean.getDepot().getId())
                        .addressLines(bean.getDepot().getAddressLines())
                        .latitude(bean.getDepot().getLatitude())
                        .longitude(bean.getDepot().getLongitude())
                        .build())
                .customers(bean.getCustomers().stream().map(
                        c -> Customer.builder()
                                .name(c.getName())
                                .phoneNumber(c.getPhoneNumber())
                                .addressLines(c.getAddressLines())
                                .specialRequirements(c.getSpecialRequirements())
                                .latitude(c.getLatitude())
                                .longitude(c.getLongitude())
                                .packages(c.getPackages().stream().map(
                                        p -> Package.builder()
                                                .type(p.getType())
                                                .weight(p.getWeight())
                                                .volume(p.getVolume())
                                                .cost(p.getCost())
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                ).collect(Collectors.toSet()))
                .build();
    }

    private RoutingSessionView buildRoutingSessionView(RoutingSession routingSession) {
        return RoutingSessionView.builder()
                .id(routingSession.getId())
                .description(routingSession.getDescription())
                .vehicleIds(routingSession.getVehicles().stream().map(Vehicle::getId).collect(Collectors.toSet()))
                .customers(routingSession.getCustomers())
                .depot(routingSession.getDepot())
                .solutions(routingSession.getSolutions())
                .build();
    }

    @Override
    public Boolean makeRoutes(Long id) {
        if (activeJobs.get(id) != null)
            return false;
        log.info("Making routes for RS with id: {}", id);
        var routingSession = getById(id);
        activeJobs.put(id, routingService.makeRoutes(routingSession));
        /*var builder = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .setDateFormat("MM/dd/yy HH:mm:ss")
                .create();
        var solution = Solution.builder()
                .created(new Date())
                .routingSession(routingSession)
                .data(builder.toJson(data))
                .build();
        solutionRepository.save(solution);*/
        return true;
    }

    @SneakyThrows
    @Override
    public Boolean jobIsFinished(Long id) {
        boolean finished = activeJobs.get(id) == null || activeJobs.get(id).isDone();
        log.info(activeJobs.get(id).get().toString());
        return true;
    }


}
