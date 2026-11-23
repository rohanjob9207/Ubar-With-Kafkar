package com.rideshare.locationservice.service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String,String> redisTemplate ;
// Redis key for all drivers' locations
    private static final String DRIVER_LOCATION_KEY = "drivers:locations";

    /*
    * update driver location in Redis
    * called by driver every 3 seconds to update the location in Redis
    * Map to the GEOADD command in Redis
    * */
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
        log.info("Updating driver location for driverId: {}, latitude: {}, longitude: {}", driverLocationRequest.getDriverId());
        // IMPORTENT : longitude is first and latitude is second in Point class
        Point point = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );
      redisTemplate.opsForGeo().add(
              DRIVER_LOCATION_KEY,
              point,
              driverLocationRequest.getDriverId()
      );
      log.info("location updated for driverId: {}", driverLocationRequest.getDriverId());

    }


    // Find nearby drivers based on the rider's location and return the list of nearby drivers with their distance in km
    // Matching service call this when ride will requested
    // Map to the GEORADIUS command in Redis

    public @Nullable List<NearByDriverResponce> findNearByDrivers(double latitude, double longitude, double radiusInKm) {
        log.info("Finding nearby drivers for latitude: {}, longitude: {}, radius: {}", latitude, longitude, radiusInKm);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, org.springframework.data.geo.Metrics.KILOMETERS)
                );

        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
                redisTemplate.opsForGeo().radius(
                        DRIVER_LOCATION_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeDistance()
                                .includeCoordinates()
                                .sortAscending()
                                .limit(10));

        List<NearByDriverResponce> nearByDriverResponces = new ArrayList<>();
        if (geoResults != null){
             geoResults.getContent().forEach(result ->{
                 RedisGeoCommands.GeoLocation<String> location = result.getContent();
                 nearByDriverResponces.add(
                         new NearByDriverResponce(
                                 location.getName(),
                                 location.getPoint().getY(),
                                    location.getPoint().getX(),
                                    result.getDistance().getValue()

                         )
                 );
                 log.info("find nearby driver: driverId: {}, latitude: {}, longitude: {}, distanceInKm: {}",
                        nearByDriverResponces.size()
                 );
             });
        }
        return nearByDriverResponces;
    }

    /*
    * remove driver from Redis when driver goes offline
    * called by driver when goes offline
    * Map to the ZREM command in Redis
    *
    * */
    public void removeDriver(String driverId) {
        log.info("Removing driver from Redis for driverId: {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVER_LOCATION_KEY, driverId);
        log.info("Driver removed from Redis for driverId: {}", driverId);
    }
}
