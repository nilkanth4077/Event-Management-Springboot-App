package com.event_management.dto;

import com.event_management.entities.Event;
import com.event_management.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {

    private Long          id;
    private String        title;
    private String        details;
    private String        thumbnail;
    private LocalDateTime date;
    private String        type;
    private String        location;
    private int           capacity;
    private int           price;
    private String        badge;
    private EventStatus   status;
    private LocalDateTime createdAt;

    // Host summary — avoid sending full User object
    private Long   hostId;
    private String hostName;
    private String hostEmail;

    public static EventResponse from(Event e) {
        EventResponse r = new EventResponse();
        r.setId(e.getId());
        r.setTitle(e.getTitle());
        r.setDetails(e.getDetails());
        r.setThumbnail(e.getThumbnail());
        r.setDate(e.getDate());
        r.setType(e.getType());
        r.setLocation(e.getLocation());
        r.setCapacity(e.getCapacity());
        r.setPrice(e.getPrice());
        r.setBadge(e.getBadge());
        r.setStatus(e.getStatus());
        r.setCreatedAt(e.getCreatedAt());

        if (e.getHost() != null) {
            r.setHostId(e.getHost().getId());
            r.setHostName(e.getHost().getFirstName() + " " + e.getHost().getLastName());
            r.setHostEmail(e.getHost().getEmail());
        }
        return r;
    }
}