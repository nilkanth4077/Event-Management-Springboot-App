package com.event_management.services;

import com.event_management.dto.OrganizerApplicationRequest;
import com.event_management.dto.OrganizerUpdateRequest;
import com.event_management.dto.ReqRes;
import com.event_management.dto.StandardDTO;
import com.event_management.entities.OrganizerProfile;
import com.event_management.entities.User;
import com.event_management.enums.ApprovalStatus;
import com.event_management.enums.OrganizerType;
import com.event_management.jwt.JwtUtils;
import com.event_management.repositories.OrganizerProfileRepo;
import com.event_management.repositories.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerProfileRepo organizerProfileRepository;
    private final UserRepo userRepository;
    private final JwtUtils jwtUtils;

    public StandardDTO<OrganizerProfile> applyAsOrganizer(
            String authHeader,
            OrganizerApplicationRequest request
    ) {
        // Extract email from JWT
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtils.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        ;
        if (user == null) {
            return new StandardDTO<>(404, "User not found", null, null);
        }

        // Prevent duplicate applications
        if (organizerProfileRepository.existsByUser(user)) {
            return new StandardDTO<>(409, "You have already submitted an organizer application.", null, null);
        }

        // Build profile
        OrganizerProfile profile = new OrganizerProfile();
        profile.setUser(user);
        profile.setOrganizationName(request.getOrganizationName());
        profile.setOrganizerType(OrganizerType.valueOf(request.getOrganizerType()));
        profile.setCollegeName(request.getCollegeName());
        profile.setCity(request.getCity());
        profile.setDescription(request.getDescription());
        profile.setWebsite(request.getWebsite());
        profile.setInstagram(request.getInstagram());
        profile.setExpectedEventsPerMonth(request.getExpectedEventsPerMonth());
        profile.setStatus(ApprovalStatus.PENDING);

        OrganizerProfile saved = organizerProfileRepository.save(profile);

        return new StandardDTO<>(200, "Application submitted successfully. You'll be notified within 24–48 hours.", saved, null);
    }

    public StandardDTO<List<OrganizerProfile>> getAllOrganizers() {
        List<OrganizerProfile> organizers = organizerProfileRepository.findAll();
        return new StandardDTO<>(200, "Fetched list of organizers successfully.", organizers, null);
    }

    public long countByStatus(String status) {
        return organizerProfileRepository.countByStatus(ApprovalStatus.valueOf(status));
    }

    @Transactional
    public OrganizerProfile updateOrganizer(Long id, OrganizerUpdateRequest req) {

        OrganizerProfile profile = organizerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer profile not found with id: " + id));

        // ── User fields ───────────────────────────────────────────────
        User user = profile.getUser();

        if (req.getFirstName() != null)
            user.setFirstName(req.getFirstName());

        if (req.getLastName() != null)
            user.setLastName(req.getLastName());

        if (req.getEmail() != null)
            user.setEmail(req.getEmail());

        userRepository.save(user);

        // ── OrganizerProfile fields ───────────────────────────────────
        if (req.getOrganizationName() != null)
            profile.setOrganizationName(req.getOrganizationName());

        if (req.getCollegeName() != null)
            profile.setCollegeName(req.getCollegeName());

        if (req.getCity() != null)
            profile.setCity(req.getCity());

        if (req.getDescription() != null)
            profile.setDescription(req.getDescription());

        if (req.getWebsite() != null)
            profile.setWebsite(req.getWebsite());

        if (req.getInstagram() != null)
            profile.setInstagram(req.getInstagram());

        if (req.getExpectedEventsPerMonth() != null)
            profile.setExpectedEventsPerMonth(req.getExpectedEventsPerMonth());

        // ── Enum fields — parse safely ────────────────────────────────
        if (req.getOrganizerType() != null) {
            try {
                profile.setOrganizerType(OrganizerType.valueOf(req.getOrganizerType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid organizer type: " + req.getOrganizerType());
            }
        }

        if (req.getStatus() != null) {
            try {
                profile.setStatus(ApprovalStatus.valueOf(req.getStatus().toUpperCase()));
                if (Objects.equals(req.getStatus(), "APPROVED")) {
                    user.setRole("ORGANIZER");
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + req.getStatus());
            }
        }

        // appliedAt is @Column(updatable = false) — JPA ignores it automatically

        return organizerProfileRepository.save(profile);
    }
}