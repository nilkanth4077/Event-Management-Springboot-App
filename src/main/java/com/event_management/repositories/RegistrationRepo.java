package com.event_management.repositories;

import com.event_management.entities.Event;
import com.event_management.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepo extends JpaRepository<Registration, Long> {

    long countByEventId(Long eventId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    List<Registration> findByUserId(Long userId);
}
