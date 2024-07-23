package com.event_management.services;

import com.event_management.dto.ScheduleDTO;
import com.event_management.entities.Event;
import com.event_management.entities.Guest;
import com.event_management.entities.Schedule;
import com.event_management.repositories.EventRepo;
import com.event_management.repositories.ScheduleRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ScheduleService {

    private ScheduleRepo scheduleRepo;
    private EventRepo eventRepo;
    private EventService eventService;

    public ScheduleService(ScheduleRepo scheduleRepo, EventRepo eventRepo, EventService eventService){
        this.scheduleRepo = scheduleRepo;
        this.eventRepo = eventRepo;
        this.eventService = eventService;
    }

    public Schedule createScheduleItem(ScheduleDTO scheduleDTO, Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Schedule schedule = new Schedule();
        schedule.setEvent(event);
        schedule.setDate(scheduleDTO.getDate());
        schedule.setSubEvent(scheduleDTO.getSubEvent());
        schedule.setBy(scheduleDTO.getBy());
        schedule.setDay(String.valueOf(scheduleDTO.getDate().getDayOfWeek()));
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());

        return scheduleRepo.save(schedule);
    }

    public String deleteScheduleItem(Long itemId){
        Optional<Schedule> schedule = scheduleRepo.findById(itemId);
        if(schedule.isPresent()){
            scheduleRepo.deleteById(itemId);
            return "Schedule item deleted successfully: " + itemId;
        } else {
            throw new NoSuchElementException("Schedule item not found with id: " + itemId);
        }
    }

    public List<Schedule> scheduleByEvent(Long eventId){
        Optional<Event> event = eventService.getEventById(eventId);
        if(event.isPresent()){
            List<Schedule> scheduleItems = scheduleRepo.findByEventId(eventId);
            return scheduleItems;
        } else {
            throw new NoSuchElementException("List not found");
        }
    }

}
