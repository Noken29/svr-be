package com.noken29.vrpjobs.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VrpDepot extends Node {
    public VrpDepot(long id, BigDecimal lat, BigDecimal lng) {
        super(id, lat, lng);
    }
}
