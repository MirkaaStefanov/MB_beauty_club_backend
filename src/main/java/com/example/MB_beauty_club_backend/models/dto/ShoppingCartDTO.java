package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.models.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartDTO {

    private Long id;
    private PublicUserDTO user;
    private boolean deleted;

}
