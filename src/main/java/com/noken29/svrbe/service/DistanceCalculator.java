package com.noken29.svrbe.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.noken29.svrbe.domain.Customer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistanceCalculator {

    private static final String API_KEY = "AIzaSyB_ejztzFPAW0W8Cb6He9lDRs5kmr45BSc";

    public void calculate(List<Customer> customers) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        Map<String, String> origins = new HashMap<>();
        customers.forEach(c ->
                origins.put(c.getId().toString(), c.getLatitude() + "," + c.getLongitude())
        );

        Map<String, String> destinations = origins;

        try {
            String[] originsArray = origins.values().toArray(new String[0]);
            String[] destinationsArray = destinations.values().toArray(new String[0]);

            DistanceMatrixApiRequest request = DistanceMatrixApi.getDistanceMatrix(context, originsArray, destinationsArray);
            DistanceMatrix distanceMatrix = request.await();

            if (distanceMatrix != null) {
                DistanceMatrixElementStatus status = distanceMatrix.rows[0].elements[0].status;
                if (status == DistanceMatrixElementStatus.OK) {
                    for (String originKey : origins.keySet()) {
                        for (String destinationKey : destinations.keySet()) {
                            int originIndex = Arrays.asList(originsArray).indexOf(origins.get(originKey));
                            int destinationIndex = Arrays.asList(destinationsArray).indexOf(destinations.get(destinationKey));
                            long distance = distanceMatrix.rows[originIndex].elements[destinationIndex].distance.inMeters; // Distance in kilometers
                            System.out.println("Distance from " + originKey + " to " + destinationKey + ": " + distance + " km");
                        }
                    }
                } else {
                    System.out.println("Error: " + status);
                }
            } else {
                System.out.println("Error: Distance Matrix is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
