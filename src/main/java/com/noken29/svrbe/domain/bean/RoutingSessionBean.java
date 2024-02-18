package com.noken29.svrbe.domain.bean;

import com.noken29.svrbe.domain.Vehicle;
import lombok.Data;

import java.util.List;

@Data
public class RoutingSessionBean {
    private String description;
    private List<CustomerBean> customers;
    private List<Vehicle> vehicles;
}
