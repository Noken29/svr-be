package com.noken29.svrbe.domain.bean;

import lombok.Data;

@Data
public class PackageBean {
    private Long id;
    private Long customerId;
    private String type;
    private double weight;
    private double volume;
    private double cost;
}
