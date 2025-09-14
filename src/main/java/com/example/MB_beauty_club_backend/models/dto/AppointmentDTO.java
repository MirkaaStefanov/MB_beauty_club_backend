package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.enums.AppointmentStatus;
import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.models.entity.Service;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;
    private WorkerDTO worker;
    private PublicUserDTO user;
    private ServiceDTO service;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status = AppointmentStatus.PENDING;
    private boolean deleted;

}
