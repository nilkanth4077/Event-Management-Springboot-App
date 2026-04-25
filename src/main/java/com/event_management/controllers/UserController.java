package com.event_management.controllers;

import com.event_management.dto.LoginRequest;
import com.event_management.dto.RegRequest;
import com.event_management.dto.ReqRes;
import com.event_management.dto.StandardDTO;
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
    public ResponseEntity<StandardDTO<List<Event>>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            return ResponseEntity.ok(
                    new StandardDTO<>(HttpStatus.OK.value(), "Events fetched successfully", events, null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new StandardDTO<>(HttpStatus.NOT_FOUND.value(), "Error fetching events", null, null)
            );
        }
    }
}
