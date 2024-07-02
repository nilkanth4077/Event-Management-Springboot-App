package com.event_management.services;

import com.event_management.entities.Event;
import com.event_management.entities.User;
import com.event_management.repositories.EventRepo;
import com.event_management.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public EventService(EventRepo eventRepo, UserRepo userRepo, KafkaTemplate<String, Object> kafkaTemplate){
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Create event
    public Event addEvent(Event event, Long userId){
        Optional<User> user = userRepo.findById(userId);

        if (user.isPresent() && ("ORGANIZER".equals(user.get().getRole()) || "ADMIN".equals(user.get().getRole()))){
            event.setHost(user.get());
            event.setDate(LocalDateTime.now());
            if("Virtual".equals(event.getType())){
                event.setLocation("Virtual");
            } else if (event.getHost() == null) {
                event.setHost(user.get());
            } else if (event.getPrice() == 0) {
                event.setPrice(-1);
            }
            Event savedEvent = eventRepo.save(event);
            kafkaTemplate.send("events", "Event created: " + event.getTitle());
            return savedEvent;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Delete event
    public void deleteEvent(Long userId, Long eventId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        Optional<Event> eventOptional = eventRepo.findById(eventId);
        if (!eventOptional.isPresent()) {
            throw new RuntimeException("Event not found");
        }

        Event event = eventOptional.get();
        if (!event.getHost().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this event");
        }

        eventRepo.delete(event);
        kafkaTemplate.send("events", "Event deleted: " + event.getTitle());
    }

    // Get all events
    public List<Event> getAllEvents(){
        return eventRepo.findAll();
    }

}
