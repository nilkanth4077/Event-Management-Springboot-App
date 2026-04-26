package com.event_management.entities;

import com.event_management.enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonIgnoreProperties({"password", "events", "authorities",
            "accountNonExpired", "accountNonLocked",
            "credentialsNonExpired", "enabled", "username"})
    private User host;

    private String details;

    private String thumbnail;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    private String type;

    private String location;

    private int capacity;

    private int price;

    private String badge;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Registration> registrations;
}
