package com.noken29.vrpjobs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VrpVehicle extends VrpEntity {
    private final double maxWeight;
    private final double maxVolume;
    private final double serviceCost;

    public VrpVehicle(long id, double maxWeight, double maxVolume, double serviceCost) {
        super(id);
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.serviceCost = serviceCost;
    }
}