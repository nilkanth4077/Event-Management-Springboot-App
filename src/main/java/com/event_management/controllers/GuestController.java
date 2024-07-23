package com.event_management.controllers;

import com.event_management.entities.Event;
import com.event_management.entities.Guest;
import com.event_management.services.EventService;
import com.event_management.services.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class GuestController {

    private GuestService guestService;
    private EventService eventService;

    public GuestController(GuestService guestService, EventService eventService){
        this.guestService = guestService;
        this.eventService = eventService;
    }

    @PostMapping("/guest/{eventId}")
    public ResponseEntity<Guest> addGuest(@PathVariable Long eventId, @RequestBody Guest guestBody){
        try {
            Optional<Event> event = eventService.getEventById(eventId);
            if(event.isPresent()){
                Guest guest = guestService.addGuests(eventId, guestBody);
                return new ResponseEntity<>(guest, HttpStatus.OK);
            } else {
                throw new NoSuchElementException("No such event");
            }
        } catch (Exception e) {
            throw new NoSuchElementException("Event not exist");
        }
    }

    @GetMapping("/guest/by-event/{eventId}")
    public List<Guest> findGuestsByEvent(@PathVariable Long eventId){
        try {
            List<Guest> guests = guestService.guestByEvent(eventId);
            return guests;
        } catch (Exception e){
            throw new NoSuchElementException("Not found");
        }
    }

    @DeleteMapping("/guest/delete/{guestId}")
    public String deleteGuest(@PathVariable Long guestId){
        try {
            guestService.deleteGuest(guestId);
            return "Guest deleted";
        } catch (Exception e) {
            throw new NoSuchElementException("Guest not found");
        }
    }

}
