package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponce;
import com.rideshare.rideservice.event.RideRequestedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private final RideRepo rideRepo;
    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;

    private static final String RIDE_REQUESTED_TOPIC = "ride-requests";

    /*
    * Create ride in DB with requested status
    * Publish RideRequestedEvent to Kafka topic
    * */

    public RideResponce requestRide(RideRequest rideRequest){
        log.info("Creating ride in DB with requested status for rider: {}", rideRequest.getRiderId());
        // Create ride in DB with requested status
        Ride ride = new Ride();
        ride.setRiderId(rideRequest.getRiderId());
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDropoffLatitude(rideRequest.getDropoffLatitude());
        ride.setDropoffLongitude(rideRequest.getDropoffLongitude());
        ride.setDropoffAddress(rideRequest.getDropoffAddress());
        ride.setStatus(com.rideshare.rideservice.model.RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateFare(rideRequest));
        Ride savedRide = rideRepo.save(ride);

        // Publish RideRequestedEvent to Kafka topic
        //Matching service will listen to this topic and assign a driver to the ride
        RideRequestedEvent rideRequestedEvent = new RideRequestedEvent(
                 savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickupAddress(),
                savedRide.getDropoffLatitude(),
                savedRide.getDropoffLongitude(),
                savedRide.getDropoffAddress()
        );
      kafkaTemplate.send(RIDE_REQUESTED_TOPIC,savedRide.getId(),rideRequestedEvent);
        log.info("Published RideRequestedEvent to Kafka topic: {}", savedRide.getId());

        savedRide.setStatus(RideStatus.MATCHING);
        rideRepo.save(savedRide);
        return mapToRideResponce(savedRide);

    }

    public RideResponce startRide(String rideId){
        log.info("Starting ride with ID: {}", rideId);
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (ride.getStatus() == RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(java.time.LocalDateTime.now());
        Ride updatedRide = rideRepo.save(ride);
        return mapToRideResponce(updatedRide);
    }

    public RideResponce completeRide(String rideId){
        log.info("Completing ride with ID: {}", rideId);
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (ride.getStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride cannot be completed. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.RIDE_COMPLETED);
        ride.setCompletedAt(java.time.LocalDateTime.now());
        ride.setAtualFare(ride.getEstimatedFare());
        Ride updatedRide = rideRepo.save(ride);
        return mapToRideResponce(updatedRide);
    }

    public RideResponce cancelRide(String rideId){
        log.info("Cancelling ride with ID: {}", rideId);
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (ride.getStatus() == RideStatus.ACCEPTED || ride.getStatus() == RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride cannot be cancelled. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.CANCELLED);
        Ride updatedRide = rideRepo.save(ride);
        return mapToRideResponce(updatedRide);
    }

    public RideResponce getRideById(String rideId){
        log.info("Fetching ride with ID: {}", rideId);
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        return mapToRideResponce(ride);
    }

    public List<RideResponce> getRidesByRider(String riderId){
        log.info("Fetching rides for rider with ID: {}", riderId);
        List<Ride> rides = rideRepo.findByRiderIdOrderByCreatedAtDesc(riderId);
        return rides.stream().map(this::mapToRideResponce).toList();
    }

   private double calculateFare(RideRequest rideRequest) {
        // Implement fare calculation logic here
        // For simplicity, let's assume a flat rate of $10 for now
       double lat1 = Math.toRadians(rideRequest.getPickupLatitude());
       double lat2 = Math.toRadians(rideRequest.getDropoffLatitude());
       double lon1 = Math.toRadians(rideRequest.getPickupLongitude());
       double lon2 = Math.toRadians(rideRequest.getDropoffLongitude());
       double latitudeDifference = lat2 - lat1;
       double longitudeDifference = lon2 - lon1;
         double a = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c; // Distance in kilometers (Earth's radius is approximately 6371 km)
        double fare = 50 +(distance * 12); // Assuming a rate of $2 per kilometer
        return Math.round(fare * 100.0) / 100.0 ; // Round to 2 decimal places;
    }

    public void updateRideWithDriver(String rideId, String driverId){
        log.info("Updating ride with ID: {} with driver ID: {}", rideId, driverId);
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepo.save(ride);
    }

    private RideResponce mapToRideResponce(Ride ride){
        return new RideResponce(
               ride.getId(),
                ride.getRiderId(),
                ride.getDriverId(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getPickupAddress(),
                ride.getDropoffLatitude(),
                ride.getDropoffLongitude(),
                ride.getDropoffAddress(),
                ride.getStatus(),
                ride.getEstimatedFare(),
                ride.getAtualFare(),
                ride.getStartedAt(),
                ride.getCompletedAt(),
                ride.getCreatedAt(),
                ride.getUpdateAt()
        );
    }
}
