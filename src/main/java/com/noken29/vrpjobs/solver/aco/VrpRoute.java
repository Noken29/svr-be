package com.noken29.vrpjobs.solver.aco;

import java.util.List;

import com.noken29.vrpjobs.model.VrpCustomer;
import com.noken29.vrpjobs.model.VrpPackage;
import com.noken29.vrpjobs.model.VrpVehicle;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VrpRoute {
    private final VrpVehicle vehicle;
    private final List<VrpCustomer> customers;
    private final List<VrpPackage> packages;
    private final AcoContext.PackageChoosingStrategy pcs;

    private final double totalWeight;
    private final double totalVolume;
    private final double length;
    private final double cost;

    public VrpRoute(VrpVehicle vehicle,
                    List<VrpCustomer> customers,
                    List<VrpPackage> packages,
                    AcoContext.PackageChoosingStrategy pcs,
                    double totalWeight,
                    double totalVolume,
                    double length) {
        this.vehicle = vehicle;
        this.customers = customers;
        this.packages = packages;
        this.pcs = pcs;
        this.totalWeight = totalWeight;
        this.totalVolume = totalVolume;
        this.length = length;
        this.cost = vehicle.getServiceCost() * length;
    }
}

