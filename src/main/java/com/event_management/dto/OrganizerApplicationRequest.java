package com.event_management.dto;

import lombok.Data;

@Data
public class OrganizerApplicationRequest {
    private String organizationName;
    private String organizerType;
    private String collegeName;
    private String city;
    private String description;
    private String website;
    private String instagram;
    private String expectedEventsPerMonth;
}