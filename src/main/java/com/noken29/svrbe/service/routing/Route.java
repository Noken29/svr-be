package com.noken29.svrbe.service.routing;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Expose
    private Long vehicleId;
    @Expose
    private List<Long> customersIds;
    @Expose
    private List<Long> packagesIds;
    @Expose
    private double length;
    @Expose
    private double cost;
}
