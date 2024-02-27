package com.noken29.svrbe.domain.bean;

import com.noken29.svrbe.domain.Depot;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RoutingSessionBean {
    private String description;
    private Set<Long> vehicleIds;
    private List<CustomerBean> customers;
    private Depot depot;
}
