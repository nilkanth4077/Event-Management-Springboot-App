package com.event_management.controllers;

import com.event_management.entities.Event;
import com.event_management.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(value = "/adminorganizer/event/create/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> createEvent(@RequestPart("event") String eventJson, @RequestPart("thumbnail") MultipartFile thumbnailFile, @PathVariable Long userId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Event event = objectMapper.readValue(eventJson, Event.class);

            Event createdEvent = eventService.addEvent(event, userId, thumbnailFile);
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

}
