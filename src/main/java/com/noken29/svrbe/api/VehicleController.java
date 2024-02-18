package com.noken29.svrbe.api;

import com.noken29.svrbe.domain.FuelType;
import com.noken29.svrbe.domain.Vehicle;
import com.noken29.svrbe.repository.FuelTypeRepository;
import com.noken29.svrbe.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/vehicles")
public class VehicleController {

    @Autowired
    private FuelTypeRepository fuelTypeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/fuel-types")
    public ResponseEntity<List<FuelType>> findAllFuelTypes() {
        return new ResponseEntity<>(fuelTypeRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> findAll() {
        return new ResponseEntity<>(vehicleRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<Vehicle>> saveAll(@RequestBody List<Vehicle> vehicles) {
        vehicleRepository.deleteAll();
        return new ResponseEntity<>(vehicleRepository.saveAll(vehicles), HttpStatus.CREATED);
    }

}
