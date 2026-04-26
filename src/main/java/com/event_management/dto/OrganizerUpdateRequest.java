package com.event_management.dto;

import lombok.Data;

@Data
public class OrganizerUpdateRequest {

    // User fields
    private String firstName;
    private String lastName;
    private String email;

    // OrganizerProfile fields
    private String organizationName;
    private String organizerType;       // String → parsed to OrganizerType enum in service
    private String collegeName;
    private String city;
    private String description;
    private String website;
    private String instagram;
    private String expectedEventsPerMonth;
    private String status;              // String → parsed to ApprovalStatus enum in service
}