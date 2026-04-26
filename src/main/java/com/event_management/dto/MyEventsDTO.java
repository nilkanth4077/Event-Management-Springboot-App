package com.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyEventsDTO {
    private List<EventResponse> upcoming;
    private List<EventResponse> past;
}