package com.rideshare.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* Event published to kafka topic "ride-matched" when a ride is matched with a driver.
* published by matching-service
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMatchedEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double distanceToPickupInKm;
}
