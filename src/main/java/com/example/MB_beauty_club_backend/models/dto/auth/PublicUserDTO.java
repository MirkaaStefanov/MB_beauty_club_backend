package com.example.MB_beauty_club_backend.models.dto.auth;

import com.example.MB_beauty_club_backend.enums.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserDTO {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private Role role;
}
