package com.noken29.svrbe.service;

import com.noken29.svrbe.api.RoutingSessionAPI;
import com.noken29.svrbe.domain.*;
import com.noken29.svrbe.domain.Package;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;
import com.noken29.svrbe.domain.view.RoutingSessionInfo;
import com.noken29.svrbe.domain.view.RoutingSessionView;
import com.noken29.svrbe.repository.RoutingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutingSessionAPIImpl implements RoutingSessionAPI {

    @Autowired
    private RoutingSessionRepository routingSessionRepository;

    @Autowired
    private VehicleService vehicleService;

    @Override
    public RoutingSessionView getById(Long id) {
        return buildRoutingSessionView(routingSessionRepository.getReferenceById(id));
    }

    @Override
    public RoutingSessionView create(RoutingSessionBean bean) {
        RoutingSession routingSession = buildRoutingSession(null, bean);
        routingSession.getCustomers().forEach(c -> c.setRoutingSession(routingSession));
        routingSession.getCustomers().forEach(c -> c.getPackages().forEach(p -> p.setCustomer(c)));
        routingSession.setVehicles(vehicleService.getVehiclesByIds(bean.getVehicleIds()));
        return buildRoutingSessionView(routingSessionRepository.save(routingSession));
    }

    @Override
    public RoutingSessionView update(Long routingSessionId, RoutingSessionBean bean) {
        RoutingSession routingSession = buildRoutingSession(routingSessionId, bean);
        routingSession.getCustomers().forEach(c -> c.setRoutingSession(routingSession));
        routingSession.getCustomers().forEach(c -> c.getPackages().forEach(p -> p.setCustomer(c)));
        routingSession.setVehicles(vehicleService.getVehiclesByIds(bean.getVehicleIds()));
        return buildRoutingSessionView(routingSessionRepository.save(routingSession));
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
                .depot(bean.getDepot())
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
                                ).collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList()))
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
}
