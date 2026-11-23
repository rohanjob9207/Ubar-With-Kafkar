package com.rideshare.matchingservice.service;

import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideEventConsumer {

    private final MatchingService matchingService;

    /*
    * Listence the request kafka topic
    * Triggered every time Ride service publish a new rise request
    *
    * Flow :
    * Ride Serice -> Kafka (ride - request) -> This Consumer -> MatchingService
    *
    * */

    @KafkaListener(
            topics = "ride.requested",
            groupId = "matching-service-group"
    )
    public void consumeRideRwquestedEvent(RideRequestedEvent matchedEvent){
        try{
            matchingService.matchDriverForRide(matchedEvent);
        }
        catch (Exception e){
            log.error("Error Processing ride request {} -{}",
                    matchedEvent.getRideId(),e.getMessage());

            //
        }
    }
}
