package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.enums.OrderStatus;
import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.models.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private UUID id;
    private PublicUserDTO user;
    private LocalDate orderDate;
    private boolean deleted;
    private boolean invoiced;
    private OrderStatus status;
    private BigDecimal price;
    private BigDecimal euroPrice;

}
