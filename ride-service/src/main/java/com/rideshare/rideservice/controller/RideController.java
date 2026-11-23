package com.rideshare.rideservice.controller;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponce;
import com.rideshare.rideservice.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponce> requestRide(
            @Valid @RequestBody RideRequest rideRequest) {
        log.info("Received ride request: {}", rideRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.requestRide(rideRequest));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponce> getRideById(@PathVariable String rideId) {
        log.info("Fetching ride with ID: {}", rideId);
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponce>> getRideByRiderId(@PathVariable String riderId) {
        log.info("Fetching rides for rider with ID: {}", riderId);
        return ResponseEntity.ok(rideService.getRidesByRider(riderId));
    }

    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponce> startRide(@PathVariable String rideId) {
        log.info("Starting ride with ID: {}", rideId);
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<RideResponce> completeRide(@PathVariable String rideId) {
        log.info("Completing ride with ID: {}", rideId);
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @DeleteMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponce> cancelRide(@PathVariable String rideId) {
        log.info("Cancelling ride with ID: {}", rideId);
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }
}
