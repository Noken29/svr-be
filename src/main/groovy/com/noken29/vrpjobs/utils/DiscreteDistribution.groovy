package com.noken29.vrpjobs.utils

static def discreteDistribution(List<BigDecimal> weights) {
    int total = weights.sum()
    def sum = 0.0
    def index = 0
    for (int i = 0; i < weights.size() && sum <= total * Math.random(); i++) {
        sum += weights[i]
        if (weights[i] != BigDecimal.ZERO)
            index = i
    }
    return index
}