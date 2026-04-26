package com.event_management.controllers;

import com.event_management.dto.*;
import com.event_management.entities.Event;
import com.event_management.entities.User;
import com.event_management.jwt.JwtUtils;
import com.event_management.repositories.RegistrationRepo;
import com.event_management.repositories.UserRepo;
import com.event_management.services.EventService;
import com.event_management.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final UserRepo userRepo;
    private final RegistrationRepo registrationRepo;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(@Lazy UserService userService, EventService eventService, UserRepo userRepo, RegistrationRepo registrationRepo, JwtUtils jwtUtils) {
        this.userService = userService;
        this.eventService = eventService;
        this.userRepo = userRepo;
        this.registrationRepo = registrationRepo;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> createUser(@RequestBody RegRequest reqData) {
        ReqRes req = new ReqRes();
        req.setFirstName(reqData.getFirstName());
        req.setLastName(reqData.getLastName());
        req.setEmail(reqData.getEmail());
        req.setPassword(reqData.getPassword());
        req.setRole("USER");
        return ResponseEntity.ok(userService.createUser(req));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody LoginRequest request) {
        ReqRes req = new ReqRes();
        req.setEmail(request.getEmail());
        req.setPassword(request.getPassword());
        return ResponseEntity.ok(userService.login(req));
    }

    @GetMapping("/events")
    public ResponseEntity<StandardDTO<List<EventResponse>>> getAllEvents() {
        try {
            List<EventResponse> events = eventService.getAllEvents();
            return ResponseEntity.ok(
                    new StandardDTO<>(HttpStatus.OK.value(), "Events fetched successfully", events, null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new StandardDTO<>(HttpStatus.NOT_FOUND.value(), "Error fetching events", null, null)
            );
        }
    }

    @PostMapping("/events/register/{eventId}")
    public ResponseEntity<StandardDTO<String>> register(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String email = jwtUtils.extractUsername(authHeader.replace("Bearer ", ""));
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        StandardDTO<String> response =
                eventService.registerUserForEvent(eventId, user.getId());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/events/registrations/check")
    public ResponseEntity<StandardDTO<Boolean>> checkRegistration(
            @RequestParam Long eventId,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        boolean exists = registrationRepo
                .existsByUserIdAndEventId(user.getId(), eventId);

        return ResponseEntity.ok(
                new StandardDTO<>(200, "Checked", exists, null)
        );
    }

    @GetMapping("/user/my-events")
    public ResponseEntity<StandardDTO<MyEventsDTO>> getMyEvents(
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        StandardDTO<MyEventsDTO> response =
                userService.getMyEvents(user.getId());

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }
}
