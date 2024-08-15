package com.event_management.controllers;

import com.event_management.dto.ReqRes;
import com.event_management.entities.User;
import com.event_management.services.PdfGenerationService;
import com.event_management.services.PdfUsingFlyingSaucer;
import com.event_management.services.UserService;
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

@RestController
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final PdfGenerationService pdfGenerationService;
    private final PdfUsingFlyingSaucer pdfUsingFlyingSaucer;

    @Autowired
    public AdminController(@Lazy UserService userService, PdfGenerationService pdfGenerationService, PdfUsingFlyingSaucer pdfUsingFlyingSaucer) {
        this.userService = userService;
        this.pdfGenerationService = pdfGenerationService;
        this.pdfUsingFlyingSaucer = pdfUsingFlyingSaucer;
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        logger.info("Getting all users");
        return ResponseEntity.ok(userService.getAllUsers());
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

}
