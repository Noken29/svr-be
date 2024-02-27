package com.noken29.vrpjobs.model

import lombok.AllArgsConstructor

@AllArgsConstructor
class Vehicle {
    int index
    BigDecimal maxWeight, maxVolume, serviceCost

    long entityId
}