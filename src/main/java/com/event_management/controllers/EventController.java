package com.event_management.controllers;

import com.event_management.entities.Event;
import com.event_management.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {

        private final EventService eventService;

        @Autowired
        public EventController(EventService eventService){
            this.eventService = eventService;
        }

        @PostMapping("/adminorganizer/event/create/{userId}")
        public ResponseEntity<Event> createEvent(@RequestBody Event event, @PathVariable Long userId){
            return ResponseEntity.ok(eventService.addEvent(event, userId));
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

}
