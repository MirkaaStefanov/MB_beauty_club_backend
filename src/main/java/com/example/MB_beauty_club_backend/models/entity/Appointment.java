package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which worker is providing the service
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    // Which customer books the appointment
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Which service is chosen
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    // Appointment details
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(name = "is_deleted")
    private boolean deleted = false;
}
