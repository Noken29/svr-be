package com.noken29.svrbe.domain.bean;

import lombok.Data;

import java.util.List;

@Data
public class CustomerBean {
    private Long id;
    private String name;
    private String phoneNumber;
    private String addressLines;
    private String specialRequirements;
    private double latitude;
    private double longitude;
    private List<PackageBean> packages;
}
