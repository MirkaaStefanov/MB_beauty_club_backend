package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.filters.JwtAuthenticationFilter;
import com.example.MB_beauty_club_backend.models.dto.auth.AdminUserDTO;
import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.services.UserService;
import com.example.MB_beauty_club_backend.services.impl.security.AuthenticationServiceImpl;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationServiceImpl authenticationService;

    @GetMapping("/all")
    public ResponseEntity<List<PublicUserDTO>> getAllUsers(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<PublicUserDTO> getMe(@RequestHeader("Authorization") String auth){
        return ResponseEntity.ok(authenticationService.me(auth));
    }

    @GetMapping("/{id}/admin")
    public ResponseEntity<AdminUserDTO> getByIdAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getByIdAdmin(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @RateLimiter(name = "general_api_rate_limiter")
    public ResponseEntity<AdminUserDTO> update(@PathVariable("id") UUID id, @RequestBody AdminUserDTO userDTO, HttpServletRequest httpServletRequest) {
        PublicUserDTO user = (PublicUserDTO) httpServletRequest.getAttribute(JwtAuthenticationFilter.USER_KEY);
        return ResponseEntity.ok(userService.updateUser(id, userDTO, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id, HttpServletRequest httpServletRequest) {
        PublicUserDTO user = (PublicUserDTO) httpServletRequest.getAttribute(JwtAuthenticationFilter.USER_KEY);
        userService.deleteUserById(id, user);
        return ResponseEntity.ok().build();
    }


}
