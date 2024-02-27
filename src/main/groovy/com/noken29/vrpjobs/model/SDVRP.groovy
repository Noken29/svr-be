package com.noken29.vrpjobs.model

class SplitDeliveryVehicleRoutingProblem {
    List<Vehicle> vehicles
    Graph graph
    Map<Integer, List<Package>> packages = [:], packagesSortedWeight, packagesSortedVolume
    Map<Integer, BigDecimal> customerTotalWeight = [:], customerTotalVolume = [:]
    BigDecimal totalWeight, totalVolume

    SplitDeliveryVehicleRoutingProblem(List<Vehicle> vehicles, Graph graph, List<Package> packages) {
        this.vehicles = vehicles
        this.graph = graph
        packages.forEach {
            if (!this.packages.containsKey(it.relatedNodeIndex))
                this.packages[it.customerIndex] = []
            this.packages[it.customerIndex].add(it)
        }
        this.packagesSortedWeight = new HashMap<>(this.packages)
        this.packagesSortedVolume = new HashMap<>(this.packages)
        for (customerIndex in this.packages.keySet()) {
            this.packagesSortedWeight[customerIndex].sort {(a, b) -> a.weight <=> b.weight ?: a.volume <=> b.volume}
            this.packagesSortedVolume[customerIndex].sort {(a, b) -> a.volume <=> b.volume ?: a.weight <=> b.weight}
        }
        for (customerIndex in this.packages.keySet()) {
            this.customerTotalWeight[customerIndex] = this.packages[customerIndex].sum {it.weight}
            this.customerTotalVolume[customerIndex] = this.packages[customerIndex].sum {it.volume}
        }
        this.totalWeight = packages.sum {it.weight}
        this.totalVolume = packages.sum {it.volume}
    }
}