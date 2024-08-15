package com.event_management.controllers;

import com.event_management.dto.ReqRes;
import com.event_management.entities.Event;
import com.event_management.entities.User;
import com.event_management.services.EventService;
import com.event_management.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public UserController(@Lazy UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }


    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> createUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password, @RequestParam String role) {
        ReqRes req = new ReqRes();
        req.setFirstName(firstName);
        req.setLastName(lastName);
        req.setEmail(email);
        req.setPassword(password);
        req.setRole(role);
        return ResponseEntity.ok(userService.createUser(req));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestParam String email, @RequestParam String password) {
        ReqRes req = new ReqRes();
        req.setEmail(email);
        req.setPassword(password);
        return ResponseEntity.ok(userService.login(req));
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }
}
