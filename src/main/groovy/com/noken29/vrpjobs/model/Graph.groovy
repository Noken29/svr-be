package com.noken29.vrpjobs.model

import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode

@AllArgsConstructor
@EqualsAndHashCode
class Node {
    int index
    BigDecimal lat, lng

    // Depot or Customer Id
    long entityId
}

class Depot extends Node {}
class Customer extends Node {}

class Graph {
    Depot depot
    Map<Integer, Customer> customers = [:]
    List<Integer> customerIndexes = []
    List<List<BigDecimal>> distanceMatrix

    Graph(Depot depot, List<Customer> customers) {
        this.depot = depot
        customers.forEach {this.customers[it.index] = it}
        this.customerIndexes = new ArrayList<>(this.customers.keySet())
        this.distanceMatrix = distanceMatrix
    }
}