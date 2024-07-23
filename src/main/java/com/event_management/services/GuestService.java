package com.event_management.services;

import com.event_management.entities.Event;
import com.event_management.entities.Guest;
import com.event_management.repositories.GuestRepo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GuestService {

    private GuestRepo guestRepo;
    private EventService eventService;

    public GuestService(GuestRepo guestRepo, EventService eventService){
        this.guestRepo = guestRepo;
        this.eventService = eventService;
    }

    public Guest addGuests(Long eventId, Guest guestBody){
        Optional<Event> event = eventService.getEventById(eventId);
        if(event.isPresent()){
            Guest guest = new Guest();
            guest.setEvent(event.orElse(null));
            guest.setName(guestBody.getName());
            guest.setRole(guestBody.getRole());
            guest.setProfile_url(guestBody.getProfile_url());
            Guest savedGuest = guestRepo.save(guest);
            return savedGuest;
        } else {
            throw new NoSuchElementException("Event not found with id: " + eventId);
        }
    }

    public List<Guest> guestByEvent(Long eventId){
        Optional<Event> event = eventService.getEventById(eventId);
        if(event.isPresent()){
            List<Guest> guests = guestRepo.findByEventId(eventId);
            return guests;
        } else {
            throw new NoSuchElementException("List not found");
        }
    }

    public String deleteGuest(Long guestId){
        Optional<Guest> guest = guestRepo.findById(guestId);
        if(guest.isPresent()){
            guestRepo.deleteById(guestId);
            return "Guest deleted successfully";
        } else {
            throw new NoSuchElementException("Guest not found with id: " + guestId);
        }
    }

}
