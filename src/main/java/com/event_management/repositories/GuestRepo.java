package com.event_management.repositories;

import com.event_management.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepo extends JpaRepository<Guest, Long> {

    List<Guest> findByEventId(Long eventId);
}
