package com.rideshare.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Topic share Ride Servuce will publish the events to matching service

    @Bean
    public NewTopic rideRequestedTopic(){
        return TopicBuilder.name("ride-requested")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topic Share Matching Service will publish the events to Ride Service

    @Bean
    public NewTopic rideMatchedTopic(){
        return TopicBuilder.name("ride-matched")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
