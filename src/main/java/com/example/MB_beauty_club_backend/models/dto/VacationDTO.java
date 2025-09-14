package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.models.entity.Worker;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacationDTO {

    private Long id;
    private WorkerDTO worker;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean deleted;

}
