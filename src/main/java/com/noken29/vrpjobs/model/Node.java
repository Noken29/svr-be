package com.noken29.vrpjobs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Node extends VrpEntity {
    private final BigDecimal lat;
    private final BigDecimal lng;

    Node(long id, BigDecimal lat, BigDecimal lng) {
        super(id);
        this.lat = lat;
        this.lng = lng;
    }

    public String formatCoordinates() {
        return lat + "," + lng;
    }
}
