package com.event_management.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    private String details;

    private String thumbnail;

    private LocalDateTime date;

    private String type;

    private String location;

    private int capacity;

    private int price;

    private String Badge;
}
