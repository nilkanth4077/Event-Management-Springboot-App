package com.event_management.controllers;

import com.event_management.dto.EventCreateRequest;
import com.event_management.dto.EventResponse;
import com.event_management.dto.OrganizerApplicationRequest;
import com.event_management.dto.StandardDTO;
import com.event_management.entities.Event;
import com.event_management.entities.OrganizerProfile;
import com.event_management.services.EventService;
import com.event_management.services.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;
    private final EventService eventService;

    @PostMapping("/apply")
    public ResponseEntity<StandardDTO<OrganizerProfile>> applyAsOrganizer(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody OrganizerApplicationRequest request
    ) {
        try {
            StandardDTO<OrganizerProfile> response = organizerService.applyAsOrganizer(authHeader, request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new StandardDTO<>(400, "Invalid organizer type: " + e.getMessage(), null, null)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new StandardDTO<>(500, "Something went wrong: " + e.getMessage(), null, null)
            );
        }
    }

    @PostMapping("/events/create")
    public ResponseEntity<StandardDTO<EventResponse>> createEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EventCreateRequest request) {

        EventResponse created = eventService.createEvent(userDetails.getUsername(), request);
        return ResponseEntity.status(201).body(
                new StandardDTO<>(201, "Event created successfully", created, null)
        );
    }

    @GetMapping("/events")
    public ResponseEntity<StandardDTO<List<EventResponse>>> getMyEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<EventResponse> events = eventService.getMyEvents(userDetails.getUsername());
        return ResponseEntity.ok(
                new StandardDTO<>(200, "Events fetched successfully", events, null)
        );
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<StandardDTO<String>> deleteEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            StandardDTO<String> response = eventService.deleteEvent(id, authHeader);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new StandardDTO<>(500, "Something went wrong: " + e.getMessage(), null, null)
            );
        }
    }

    @PatchMapping("/events/{id}/publish")
    public ResponseEntity<StandardDTO<Event>> publishEvent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            StandardDTO<Event> response = eventService.publishEvent(id, authHeader);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new StandardDTO<>(500, "Something went wrong: " + e.getMessage(), null, null)
            );
        }
    }
}