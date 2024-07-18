package com.event_management.controllers;

import com.event_management.entities.Event;
import com.event_management.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(value = "/adminorganizer/event/create/{userId}")
    public ResponseEntity<Event> createEvent(@RequestBody String eventBody, @PathVariable Long userId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Event event = objectMapper.readValue(eventBody, Event.class);

            Event createdEvent = eventService.addEvent(event, userId);
            return ResponseEntity.ok(createdEvent);
        } catch (IOException e) {
            throw new RuntimeException("Error creating the event: " + e.getMessage());
        }
    }

    @DeleteMapping("/adminorganizer/event/delete/{userId}/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            eventService.deleteEvent(userId, eventId);
            return ResponseEntity.ok("Event with id: " + eventId + " deleted successfully by user: " + userId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/adminorganizer/my-events")
    public ResponseEntity<List<Event>> getAllEventsByUser() {
        try {
            List<Event> events = eventService.getAllEventsByUser();
            return ResponseEntity.ok(events);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/events/{eventId}")
    public Optional<Event> getEventById(@PathVariable Long eventId){
        try {
            Optional<Event> event = eventService.getEventById(eventId);
            if(event.isPresent()){
                return event;
            } else {
                throw new RuntimeException("Event not exist");
            }
        } catch (Exception e) {
            throw new RuntimeException("Event not exist");
        }
    }

}
