package com.event_management.services;

import com.event_management.entities.Event;
import com.event_management.entities.Guest;
import com.event_management.repositories.GuestRepo;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GuestService {

    private GuestRepo guestRepo;
    private EventService eventService;

    public GuestService(GuestRepo guestRepo){
        this.guestRepo = guestRepo;
    }

    public Guest addGuests(Long eventId, Guest guestBody){
        Optional<Event> event = eventService.getEventById(eventId);
        if(event.isPresent()){
            Guest guest = guestRepo.save(guestBody);
            return guest;
        } else {
            throw new NoSuchElementException("Event not found with id: " + eventId);
        }
    }

}
