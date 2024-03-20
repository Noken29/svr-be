package com.noken29.vrpjobs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VrpPackage extends VrpEntity {
    private final double weight;
    private final double volume;

    public VrpPackage(long id, double weight, double volume) {
        super(id);
        this.weight = weight;
        this.volume = volume;
    }
}