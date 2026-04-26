package com.event_management.controllers;

import com.event_management.dto.OrganizerUpdateRequest;
import com.event_management.dto.ReqRes;
import com.event_management.dto.StandardDTO;
import com.event_management.entities.Event;
import com.event_management.entities.OrganizerProfile;
import com.event_management.entities.User;
import com.event_management.repositories.EventRepo;
import com.event_management.services.*;
import com.lowagie.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final EventRepo eventRepo;
    private final OrganizerService organizerService;
    private final EventService eventService;
    private final PdfGenerationService pdfGenerationService;
    private final PdfUsingFlyingSaucer pdfUsingFlyingSaucer;

    @Autowired
    public AdminController(@Lazy UserService userService, EventRepo eventRepo, OrganizerService organizerService, EventService eventService, PdfGenerationService pdfGenerationService, PdfUsingFlyingSaucer pdfUsingFlyingSaucer) {
        this.userService = userService;
        this.eventRepo = eventRepo;
        this.organizerService = organizerService;
        this.eventService = eventService;
        this.pdfGenerationService = pdfGenerationService;
        this.pdfUsingFlyingSaucer = pdfUsingFlyingSaucer;
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        logger.info("Getting all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin/all-organizers")
    public ResponseEntity<StandardDTO<List<OrganizerProfile>>> getAllOrganizers() {
        StandardDTO<List<OrganizerProfile>> response = organizerService.getAllOrganizers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/auth/get/{id}")
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

    @GetMapping("/profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes respose = userService.getMyInfo(email);
        return ResponseEntity.status(respose.getStatusCode()).body(respose);
    }

    @GetMapping("/admin/openpdf/generate-pdf")
    public ResponseEntity<ByteArrayResource> generatePdf() throws IOException, DocumentException {
        byte[] pdfContent = pdfGenerationService.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdfContent));
    }

    @GetMapping("/admin/fs/generate-pdf")
    public ResponseEntity<ByteArrayResource> generatePdfUsingFs() throws Exception {
        byte[] pdfContent = pdfUsingFlyingSaucer.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdfContent));
    }

    @PatchMapping("/admin/organizer/update/{id}")
    public ResponseEntity<StandardDTO<OrganizerProfile>> updateOrganizer(
            @PathVariable Long id,
            @RequestBody OrganizerUpdateRequest request) {
        try {
            OrganizerProfile updated = organizerService.updateOrganizer(id, request);

            return ResponseEntity.ok(
                    new StandardDTO<>(200, "Organizer updated successfully", updated, null)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(
                    new StandardDTO<>(400, e.getMessage(), null, null)
            );
        }
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<StandardDTO<Map<String, Object>>> getStats() {
        try {
            long totalUsers = userService.countByRole("USER");
            long totalOrganizers = userService.countByRole("ORGANIZER");
            long pendingOrganizers = organizerService.countByStatus("PENDING");
            long totalEvents = eventRepo.count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalOrganizers", totalOrganizers);
            stats.put("pendingOrganizers", pendingOrganizers);
            stats.put("totalEvents", totalEvents);

            return ResponseEntity.ok(new StandardDTO<>(200, "Stats fetched", stats, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new StandardDTO<>(500, e.getMessage(), null, null));
        }
    }

}
