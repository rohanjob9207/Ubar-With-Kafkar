
package com.rideshare.rideservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    @NotBlank(message = "Rider ID cannot be blank")
    private String riderId;

    @NotNull(message = "Pickup latitude is required")
    @DecimalMin(value = "-90.0", message = "Pickup latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Pickup latitude must be <= 90")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    @DecimalMin(value = "-180.0", message = "Pickup longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Pickup longitude must be <= 180")
    private Double pickupLongitude;

    @NotBlank(message = "Pickup address cannot be blank")
    private String pickupAddress;

    @NotNull(message = "Dropoff latitude is required")
    @DecimalMin(value = "-90.0", message = "Dropoff latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Dropoff latitude must be <= 90")
    private Double dropoffLatitude;

    @NotNull(message = "Dropoff longitude is required")
    @DecimalMin(value = "-180.0", message = "Dropoff longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Dropoff longitude must be <= 180")
    private Double dropoffLongitude;

    @NotBlank(message = "Dropoff address cannot be blank")
    private String dropoffAddress;

    @NotNull(message = "Estimated fare is required")
    @PositiveOrZero(message = "Estimated fare cannot be negative")
    private Double estimatedFare;
}
