package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import com.example.MB_beauty_club_backend.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {

    Optional<ShoppingCart> findByUserAndDeletedFalse(User user);
}
