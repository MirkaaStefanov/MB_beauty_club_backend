package com.example.MB_beauty_club_backend.models.dto.auth;


import com.example.MB_beauty_club_backend.enums.Provider;
import com.example.MB_beauty_club_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String surName;
    private Role role = Role.USER;
    private Provider provider = Provider.LOCAL;
}
