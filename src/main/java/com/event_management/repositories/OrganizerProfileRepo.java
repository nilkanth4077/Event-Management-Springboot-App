package com.event_management.repositories;

import com.event_management.entities.Event;
import com.event_management.entities.OrganizerProfile;
import com.event_management.entities.User;
import com.event_management.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizerProfileRepo extends JpaRepository<OrganizerProfile, Long> {
    Optional<OrganizerProfile> findByUser(User user);
    boolean existsByUser(User user);

    long countByStatus(ApprovalStatus status);
}
