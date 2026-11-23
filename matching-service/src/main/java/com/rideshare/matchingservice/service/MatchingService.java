package com.rideshare.matchingservice.service;

import com.rideshare.matchingservice.client.LocationServiceClient;
import com.rideshare.matchingservice.dto.NearByDriverResponce;
import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate kafkaTemplate;

    private static final  String RIDE_MATCHED_TOPI="ride-matched";
    private static final double DEFAULT_SEARCH_REDUIS=5.0;

    /*
    * main matching algorith
    * called when RideRequestedEvent is consumed for kafka
    * @Param rideRequestedEvent
    *
    * STEPS:
    * 1. Ask Location Service for nearBy Drivers
    * */
    public void matchDriverForRide(RideRequestedEvent rideRequestedEvent){
        List<NearByDriverResponce> nearByDriverResponces = locationServiceClient.getNearByDriver(
                rideRequestedEvent.getPickupLatitude(),
                rideRequestedEvent.getPickupLongitude(),
                DEFAULT_SEARCH_REDUIS
        );

        if(nearByDriverResponces.isEmpty()){
            log.info("No drivers from near ride ");
            return;
        }

        // STEP 2: Score each driver and pick the best one

        Optional<NearByDriverResponce> bestDriver = findBestDriver(nearByDriverResponces);
        if(bestDriver.isEmpty()){
            log.info("could not find suitable driver for ride");
            return;
        }

        NearByDriverResponce assignedDriver = bestDriver.get();

        //STEP 3: Publish ride matched event to kafka
        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                rideRequestedEvent.getRideId(),
                rideRequestedEvent.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPI,rideRequestedEvent.getRideId(), matchedEvent);
        log.info("Ride matched Event Published");
    }

    /*
    * Driver Scaring algorithm
    *
    * Distance :70%
    * Rating : 10
    *
    * score = (1/ distance) * distanceWeight
    *
    * @Param drivers
    * @return
    * */
    private Optional<NearByDriverResponce> findBestDriver(
            List<NearByDriverResponce> drivers
    ){
         double distanceWieght = 0.7;
         double ratingWieght = 0.3;

         return drivers.stream()
                 .max(Comparator.comparingDouble(driver ->{
                     // Distance score : closer * higher score
                     // Add 0.1 to avoid division by zero

                     double distanceScore = 1.0 /(driver.getDistanceInKm()+ 0.1);

                     // similated rating 4.0 to 5.0
                     // In production fetch form driver sevice

                     double simulatedRating = 4.0 + Math.random();

                     //final weighted score
                     return (distanceScore * distanceWieght)
                             + (simulatedRating * ratingWieght);

        }));
    }
}
