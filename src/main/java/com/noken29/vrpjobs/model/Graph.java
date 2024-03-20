package com.noken29.vrpjobs.model;

import com.noken29.vrpjobs.utils.ComplexKey;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
public class Graph {
    private final VrpDepot depot;
    private final Map<String, VrpCustomer> customers = new LinkedHashMap<>();
    private final List<String> customersIndexes;
    private final Map<ComplexKey, Double> distanceMatrix = new HashMap<>();

    Graph(VrpDepot depot, List<VrpCustomer> customers, List<List<Double>> distanceMatrix) {
        this.depot = depot;
        customers.forEach(customer -> this.customers.put(customer.getIndex(), customer));
        this.customersIndexes = new ArrayList<>(this.customers.keySet());
        this.fillDistanceMatrix(distanceMatrix);
    }

    private void fillDistanceMatrix(List<List<Double>> distanceMatrix) {
        if (distanceMatrix.size() != distanceMatrix.get(0).size())
            throw new IllegalArgumentException("Distance matrix should have equal number of rows and columns.");

        if (distanceMatrix.size() != customersIndexes.size() + 1)
            throw new IllegalArgumentException("Distance matrix length should be equal to length of nodes.");

        for (int i = 0; i < customersIndexes.size(); i++) {
            String second = this.customersIndexes.get(i);
            this.distanceMatrix.put(ComplexKey.from(getDepotIndex(), second), (distanceMatrix.get(0).get(i + 1) + distanceMatrix.get(i + 1).get(0)) / 2.0);
        }

        for (int i = 0; i < customersIndexes.size(); i++) {
            String first = this.customersIndexes.get(i);
            for (int j = i; j < customersIndexes.size(); j++) {
                String second = this.customersIndexes.get(j);
                if (!first.equals(second)) {
                    this.distanceMatrix.put(ComplexKey.from(first, second), (distanceMatrix.get(i + 1).get(j + 1) + distanceMatrix.get(j + 1).get(i + 1)) / 2.0);
                }
            }
        }
    }

    public String getDepotIndex() {
        return this.depot.getIndex();
    }

    public VrpCustomer getCustomer(String key) {
        return this.customers.get(key);
    }

    public Double getDistanceBetween(String first, String second) {
        return this.distanceMatrix.get(ComplexKey.from(first, second));
    }

    public Double getDistanceFromDepot(String second) {
        return getDistanceBetween(getDepotIndex(), second);
    }
}
