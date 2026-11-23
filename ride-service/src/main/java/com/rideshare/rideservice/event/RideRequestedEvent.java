package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* Event class representing a ride request event in the ride service.
* matching service will listen to this event and assign a driver to the ride request.
* TOPIC: ride-requested
* */
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
