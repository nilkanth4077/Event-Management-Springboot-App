package com.event_management.controllers;

import com.event_management.dto.ReqRes;
import com.event_management.entities.User;
import com.event_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(@Lazy UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin/get/{id}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes respose = userService.getMyInfo(email);
        return ResponseEntity.status(respose.getStatusCode()).body(respose);
    }

}
