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
    public ResponseEntity<ReqRes> createUser(@RequestBody ReqRes req) {
        return ResponseEntity.ok(userService.createUser(req));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(userService.login(reqRes));
    }

//    @GetMapping("/logout")
//    public ResponseEntity<String> logoutUser(HttpSession session) {
//        session.invalidate();
//        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
//    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }
}
