package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndDeletedFalse(Long id);
}
