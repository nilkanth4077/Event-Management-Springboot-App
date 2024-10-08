package com.event_management.services;

import com.event_management.entities.Event;
import com.event_management.entities.User;
import com.event_management.repositories.EventRepo;
import com.event_management.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepo eventRepo;
    private final UserRepo userRepo;

    @Autowired
    public EventService(EventRepo eventRepo, UserRepo userRepo) {
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    // Create event
    public Event addEvent(Event event, Long userId) throws IOException {
        Optional<User> user = userRepo.findById(userId);

        if (user.isPresent() && ("ORGANIZER".equals(user.get().getRole()) || "ADMIN".equals(user.get().getRole()))) {
            event.setHost(user.get());
            event.setDate(LocalDateTime.now());
            if ("Virtual".equals(event.getType())) {
                event.setLocation("Virtual");
            } else if (event.getHost() == null) {
                event.setHost(user.get());
            } else if (event.getPrice() == 0) {
                event.setPrice(-1);
            }

            Event savedEvent = eventRepo.save(event);
            return savedEvent;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Delete event
    public void deleteEvent(Long userId, Long eventId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<Event> eventOptional = eventRepo.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        Event event = eventOptional.get();
        if(event.getHost().getId().equals(userId) || "ADMIN".equals(userOptional.get().getRole())){
            eventRepo.delete(event);
        } else {
            throw new RuntimeException("Unauthorized to delete this event");
        }
    }

    // Get all events
    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    // Get all events by user
    public List<Event> getAllEventsByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email);

        if(user == null){
            throw new RuntimeException("User not found");
        } else {
            return eventRepo.findByHostId(user.getId());
        }
    }

    // Get event by id
    public Optional<Event> getEventById(Long eventId){
        try {
            Optional<Event> event = eventRepo.findById(eventId);
            if(event.isPresent()){
                return event;
            } else {
                throw new RuntimeException("No such event exists");
            }
        } catch (Exception e){
            throw new NoSuchElementException("Event with id: " + eventId + " not found");
        }
    }

}
