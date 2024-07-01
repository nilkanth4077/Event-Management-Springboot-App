package com.event_management.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "events", groupId = "event_group")
    public void consume(String message){
        System.out.println("Received message: " + message);
    }

}
