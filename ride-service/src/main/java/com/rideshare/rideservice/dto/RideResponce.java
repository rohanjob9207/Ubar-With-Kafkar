package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponce {

    private String id;
    private String rideId;
    private String driverId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropoffLatitude;
    private double dropoffLongitude;
    private String dropoffAddress;
    private RideStatus status; // e.g., REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED
    private double estimatedFare;
    private double atualFare;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
