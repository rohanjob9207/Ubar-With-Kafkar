package com.rideshare.matchingservice.event;

/*
* Event Consumed from kafka topic "ride-requested" when a ride is requested by a rider.
* publis hed by ride-service
* */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {

    private String rideId;
    private String riderId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropoffLatitude;
    private double dropoffLongitude;
    private String dropoffAddress;
}
