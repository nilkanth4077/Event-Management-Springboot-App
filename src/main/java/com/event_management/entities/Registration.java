package com.event_management.entities;

import com.event_management.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who registered
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // for which event
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}