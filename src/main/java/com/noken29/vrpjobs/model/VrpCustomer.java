package com.noken29.vrpjobs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VrpCustomer extends Node {
    private final List<VrpPackage> packages;
    private final List<VrpPackage> packagesSortedWeight;
    private final List<VrpPackage> packagesSortedVolume;
    private final double totalWeight;
    private final double totalVolume;

    public VrpCustomer(long id, BigDecimal lat, BigDecimal lng, List<VrpPackage> packages) {
        super(id, lat, lng);
        this.packages = packages;
        this.packagesSortedWeight = new ArrayList<>(packages);
        this.packagesSortedWeight.sort(
                Comparator.comparing(VrpPackage::getWeight)
                .thenComparing(VrpPackage::getVolume)
        );
        this.packagesSortedVolume = new ArrayList<>(packages);
        this.packagesSortedVolume.sort(
                Comparator.comparing(VrpPackage::getVolume)
                .thenComparing(VrpPackage::getWeight)
        );
        this.totalWeight = packages.stream().map(VrpPackage::getWeight).reduce(0.0, Double::sum);
        this.totalVolume = packages.stream().map(VrpPackage::getVolume).reduce(0.0, Double::sum);
    }
}
