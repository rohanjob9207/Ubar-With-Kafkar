package com.rideshare.matchingservice.client;

import com.rideshare.matchingservice.dto.NearByDriverResponce;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "location-service", url = "${location.service.url}")
public interface LocationServiceClient {

    @GetMapping("/api/v1/location/drivers/nearby")
     List<NearByDriverResponce> getNearByDriver(
            @RequestParam double latitude,
            @RequestParam double logitude,
            @RequestParam(defaultValue = "5.0") double radius
    );
}
