package com.noken29.vrpjobs.model

import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode

@AllArgsConstructor
@EqualsAndHashCode
class Package {
    int customerIndex
    BigDecimal weight, volume

    long entityId
}