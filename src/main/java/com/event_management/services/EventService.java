package com.event_management.services;

import com.event_management.dto.EventCreateRequest;
import com.event_management.dto.EventResponse;
import com.event_management.dto.StandardDTO;
import com.event_management.entities.Event;
import com.event_management.entities.User;
import com.event_management.enums.EventStatus;
import com.event_management.jwt.JwtUtils;
import com.event_management.repositories.EventRepo;
import com.event_management.repositories.UserRepo;
import jakarta.transaction.Transactional;
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
    private final JwtUtils jwtUtils;

    @Autowired
    public EventService(EventRepo eventRepo, UserRepo userRepo, JwtUtils jwtUtils) {
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
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

            return eventRepo.save(event);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public EventResponse createEvent(String email, EventCreateRequest req) {

        User host = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // Validate required fields
        if (req.getTitle() == null || req.getTitle().isBlank())
            throw new RuntimeException("Event title is required.");
        if (req.getDate() == null)
            throw new RuntimeException("Event date is required.");
        if (req.getLocation() == null || req.getLocation().isBlank())
            throw new RuntimeException("Location is required.");

        Event event = new Event();
        event.setHost(host);
        event.setTitle(req.getTitle().trim());
        event.setDetails(req.getDetails());
        event.setThumbnail(req.getThumbnail());
        event.setDate(req.getDate());
        event.setType(req.getType());
        event.setLocation(req.getLocation().trim());
        event.setCapacity(req.getCapacity());
        event.setPrice(req.getPrice());
        event.setBadge(req.getBadge());

        if (req.getStatus() != null) {
            try {
                event.setStatus(EventStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + req.getStatus());
            }
        }
        event.setCreatedAt(LocalDateTime.now());

        return EventResponse.from(eventRepo.save(event));
    }

    // ── Fetch all events for logged-in user ──────────────────────────
    @Transactional()
    public List<EventResponse> getMyEvents(String email) {

        User host = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return eventRepo
                .findAllByHostIdOrderByCreatedAtDesc(host.getId())
                .stream()
                .map(EventResponse::from)
                .toList();
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
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if(user == null){
            throw new RuntimeException("User not found");
        } else {
            return eventRepo.findByHostId(user.getId());
        }
    }

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

    public StandardDTO<String> deleteEvent(Long eventId, String authHeader) {
        try {
            String email = jwtUtils.extractUsername(authHeader.replace("Bearer ", ""));
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            if (user == null) {
                return new StandardDTO<>(404, "User not found", null, null);
            }

            Event event = eventRepo.findById(eventId).orElse(null);
            if (event == null) {
                return new StandardDTO<>(404, "Event not found", null, null);
            }

            if (!event.getHost().getId().equals(user.getId())) {
                return new StandardDTO<>(403, "You are not authorized to delete this event", null, null);
            }

            eventRepo.deleteById(eventId);
            return new StandardDTO<>(200, "Event deleted successfully", null, null);

        } catch (Exception e) {
            return new StandardDTO<>(500, e.getMessage(), null, null);
        }
    }

    public StandardDTO<Event> publishEvent(Long eventId, String authHeader) {
        try {
            String email = jwtUtils.extractUsername(authHeader.replace("Bearer ", ""));
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            if (user == null) {
                return new StandardDTO<>(404, "User not found", null, null);
            }

            Event event = eventRepo.findById(eventId).orElse(null);
            if (event == null) {
                return new StandardDTO<>(404, "Event not found", null, null);
            }

            if (!event.getHost().getId().equals(user.getId())) {
                return new StandardDTO<>(403, "You are not authorized to publish this event", null, null);
            }

            if (EventStatus.PUBLISHED.equals(event.getStatus())) {
                return new StandardDTO<>(409, "Event is already published", null, null);
            }

            event.setStatus(EventStatus.PUBLISHED);
            Event saved = eventRepo.save(event);
            return new StandardDTO<>(200, "Event published successfully", saved, null);

        } catch (Exception e) {
            return new StandardDTO<>(500, e.getMessage(), null, null);
        }
    }

}
