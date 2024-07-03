package com.event_management.repositories;

import com.event_management.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findByHostId(Long userId);
}
