package com.event_management.entities;

import com.event_management.enums.ApprovalStatus;
import com.event_management.enums.OrganizerType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "organizer_profiles")
public class OrganizerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String organizationName;

    @Enumerated(EnumType.STRING)
    private OrganizerType organizerType;

    private String collegeName;
    private String city;
    private String description;
    private String website;
    private String instagram;
    private String expectedEventsPerMonth;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Column(updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        status = ApprovalStatus.PENDING;
    }

}