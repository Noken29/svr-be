package com.noken29.svrbe.service.routing;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.noken29.vrpjobs.model.VrpCustomer;
import com.noken29.vrpjobs.model.VrpDepot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class DistanceCalculator {

    private static final String DEPOT_KEY_PATTER = "DEPOT(id: .id, index: .index)";
    private static final String CUSTOMER_KEY_PATTER = "CUSTOMER(id: .id, index: .index)";

    @Value("${google-maps.api-key}")
    private String API_KEY;

    public List<List<Double>> buildDistanceMatrix(VrpDepot depot, List<VrpCustomer> customers) {
        List<List<Double>> distanceMatrix = new ArrayList<>(customers.size() + 1);
        if (customers.size() < 10) {
            var originsDestinations = buildOriginsDestinations(depot, customers);
            var originsDestinationsArray = originsDestinations.values().toArray(new String[0]);
            DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(new GeoApiContext.Builder().apiKey(API_KEY).build())
                    .origins(originsDestinationsArray)
                    .destinations(originsDestinationsArray);

            try {
                DistanceMatrix gDistanceMatrix = request.await();
                if (gDistanceMatrix == null || gDistanceMatrix.rows[0].elements[0].status != DistanceMatrixElementStatus.OK)
                    throw new RuntimeException("Error on creating distance matrix");

                int i = 0;
                for (String originKey : originsDestinations.keySet()) {
                    distanceMatrix.add(new ArrayList<>(originsDestinations.size()));
                    for (String destinationKey : originsDestinations.keySet()) {
                        int originIndex = Arrays.asList(originsDestinationsArray).indexOf(originsDestinations.get(originKey));
                        int destinationIndex = Arrays.asList(originsDestinationsArray).indexOf(originsDestinations.get(destinationKey));
                        long distance = gDistanceMatrix.rows[originIndex].elements[destinationIndex].distance.inMeters;
                        distanceMatrix.get(i).add(BigDecimal.valueOf(distance).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP).doubleValue());
                    }
                    i++;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error on creating distance matrix, message: " + e.getMessage());
            }
            return distanceMatrix;
        }
        distanceMatrix.add(new ArrayList<>(customers.size() + 1));
        distanceMatrix.get(0).add(0.0);
        for (var customer : customers) {
            distanceMatrix.get(0).add(calculateDistance(
                    depot.getLat().doubleValue(),
                    depot.getLng().doubleValue(),
                    customer.getLat().doubleValue(),
                    customer.getLng().doubleValue()
            ));
        }
        for (int i = 0; i < customers.size(); i++) {
            distanceMatrix.add(new ArrayList<>());
            var from = customers.get(i);
            distanceMatrix.get(i + 1).add(calculateDistance(
                depot.getLat().doubleValue(),
                depot.getLng().doubleValue(),
                from.getLat().doubleValue(),
                from.getLng().doubleValue()
            ));
            for (int j = 0; j < customers.size(); j++) {
                if (i == j) {
                    distanceMatrix.get(i + 1).add(0.0);
                    continue;
                }
                var to = customers.get(j);
                distanceMatrix.get(i + 1).add(calculateDistance(
                        from.getLat().doubleValue(),
                        from.getLng().doubleValue(),
                        to.getLat().doubleValue(),
                        to.getLng().doubleValue())
                );
            }
        }
        return distanceMatrix;
    }

    private static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        final double EARTH_RADIUS = 6371.0;
        double deltaLat = toRadians(latitude2 - latitude1);
        double deltaLon = toRadians(longitude2 - longitude1);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(toRadians(latitude1)) * Math.cos(toRadians(latitude2)) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private Map<String, String> buildOriginsDestinations(VrpDepot depot, List<VrpCustomer> customers) {
        Map<String, String> originsDestinations = new LinkedHashMap<>();
        originsDestinations.put(
                DEPOT_KEY_PATTER.replace(".id", depot.getId().toString()).replace(".index", depot.getIndex()),
                depot.formatCoordinates()
        );
        customers.forEach(e -> originsDestinations.put(
                CUSTOMER_KEY_PATTER.replace(".id", e.getId().toString()).replace(".index", e.getIndex()),
                e.formatCoordinates()
        ));
        return originsDestinations;
    }
}
