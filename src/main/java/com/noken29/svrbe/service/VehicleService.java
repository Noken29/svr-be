package com.noken29.svrbe.service;

import com.noken29.svrbe.domain.Vehicle;
import com.noken29.svrbe.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getVehiclesByIds(Set<Long> vehicleIds) {
        return vehicleRepository.findAllById(vehicleIds);
    }
}
