package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    List<VerificationToken> findByUserAndCreatedAtBefore(User user, LocalDateTime thresholdDateTime);

    void deleteAllByUser(User user);
}
