package com.noken29.svrbe.api;

import com.noken29.svrbe.domain.*;
import com.noken29.svrbe.domain.Package;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;
import com.noken29.svrbe.repository.CustomerRepository;
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
    private CustomerRepository customerRepository;

    @Override
    public RoutingSession getById(Long id) {
        return routingSessionRepository.getReferenceById(id);
    }

    @Override
    public RoutingSession create(RoutingSessionBean bean) {
        RoutingSession routingSession = RoutingSession.builder()
                .description(bean.getDescription())
                .lastSaved(new Date())
                .customers(bean.getCustomers().stream().map(
                        c -> Customer.builder()
                                .name(c.getName())
                                .telephoneNumber(c.getPhoneNumber())
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
                .vehicles(bean.getVehicles())
                .build();
        routingSession.getCustomers().forEach(c -> c.setRoutingSession(routingSession));
        routingSession.getCustomers().forEach(c -> c.getPackages().forEach(p -> p.setCustomer(c)));
        return routingSessionRepository.save(routingSession);
    }

    @Override
    public RoutingSession update(Long routingSessionId, RoutingSessionBean bean) {
        RoutingSession routingSession = RoutingSession.builder()
                .id(routingSessionId)
                .description(bean.getDescription())
                .lastSaved(new Date())
                .customers(bean.getCustomers().stream().map(
                        c -> Customer.builder()
                                .name(c.getName())
                                .telephoneNumber(c.getPhoneNumber())
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
                .vehicles(bean.getVehicles())
                .build();
        routingSession.getCustomers().forEach(c -> c.setRoutingSession(routingSession));
        routingSession.getCustomers().forEach(c -> c.getPackages().forEach(p -> p.setCustomer(c)));
        return routingSessionRepository.save(routingSession);
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
}
