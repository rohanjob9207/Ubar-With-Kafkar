
package com.rideshare.rideservice.repository;

import com.rideshare.rideservice.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepo extends MongoRepository<Ride, String> {



    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);
}
