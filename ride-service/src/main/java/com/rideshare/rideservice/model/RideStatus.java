package com.rideshare.rideservice.model;

/*
* flow
* 1. Rider requests a ride
* 2. Ride status is set to REQUESTED
* 3. Matching service finds a nearby driver and assigns the ride to the driver
* 4. Ride status is set to ACCEPTED
* 5. Driver starts the ride
* 6. Ride status is set to IN_PROGRESS
* 7. Driver completes the ride
* 8. Ride status is set to COMPLETED
* 9. If the rider cancels the ride before the driver accepts it, the ride
* status is set to CANCELLED
*
* */
public enum RideStatus {
    REQUESTED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    MATCHING,
    RIDE_STARTED,
    RIDE_COMPLETED
}
