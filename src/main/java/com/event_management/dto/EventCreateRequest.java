package com.event_management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventCreateRequest {
    private String        title;
    private String        details;
    private String        thumbnail;
    private LocalDateTime date;
    private String        type;
    private String        location;
    private int           capacity;
    private int           price;
    private String        badge;
    private String        status;
}