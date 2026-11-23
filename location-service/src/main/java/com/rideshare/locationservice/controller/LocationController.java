package com.rideshare.locationservice.controller;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponce;
import com.rideshare.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // Method :1  to update the rider live location with driverId, latitude, and longitude and update the location in Redis
    // driver phone call this every 3 seconds to update the location in Redis

    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest driverLocationRequest){
        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Driver location updated successfully");
    }
    // Method :2 to get the nearby drivers based on the rider's location and return the list of nearby drivers with their distance in km
    // Matching seervice call this when ride will requested
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponce>> getNearByDriver(
            @RequestParam double latitude ,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radius
    ){
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude, longitude, radius));
    }


    // Method :3 Driver goes offline and remove the driver from Redis
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId){
        // Call the service to remove the driver from Redis
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }
}
