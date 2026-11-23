package com.rideshare.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* Responce receied from Location service for nearby drivers
*
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponce {

    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
