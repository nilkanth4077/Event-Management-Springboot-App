package com.event_management.controllers;

import com.event_management.dto.ScheduleDTO;
import com.event_management.entities.Guest;
import com.event_management.entities.Schedule;
import com.event_management.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/schedule/create-item/{eventId}")
    public Schedule createSchedule(@RequestBody ScheduleDTO scheduleDTO, @PathVariable Long eventId) {

        try {
            return scheduleService.createScheduleItem(scheduleDTO, eventId);
        } catch (Exception e) {
            throw new RuntimeException("Something wrong: " + e);
        }
    }

    @DeleteMapping("/schedule/delete-item/{itemId}")
    public String deleteScheduleItem(@PathVariable Long itemId){
        try {
            scheduleService.deleteScheduleItem(itemId);
            return "Schedule item deleted";
        } catch (Exception e) {
            throw new NoSuchElementException("Schedule item not found");
        }
    }

    @GetMapping("/schedule/items-by-event/{eventId}")
    public List<Schedule> findScheduleItemsByEvent(@PathVariable Long eventId){
        try {
            List<Schedule> scheduleItems = scheduleService.scheduleByEvent(eventId);
            return scheduleItems;
        } catch (Exception e){
            throw new NoSuchElementException("Not found");
        }
    }

}
