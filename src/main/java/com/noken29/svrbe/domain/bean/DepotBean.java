package com.noken29.svrbe.domain.bean;

import lombok.Data;

@Data
public class DepotBean {
    private Long id;
    private String addressLines;
    private double latitude;
    private double longitude;
}
