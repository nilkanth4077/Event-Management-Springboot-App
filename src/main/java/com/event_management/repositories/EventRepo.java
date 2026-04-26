package com.event_management.repositories;

import com.event_management.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findByHostId(Long userId);

    @Query("SELECT e FROM Event e WHERE e.host.id = :hostId ORDER BY e.createdAt DESC")
    List<Event> findAllByHostIdOrderByCreatedAtDesc(@Param("hostId") Long hostId);
}
