package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByDeletedFalse();

    Order findFirstByDeletedFalseOrderByIdDesc();

    Optional<Order> findByIdAndDeletedFalse(UUID id);
    List<Order> findAllByUserAndDeletedFalse(User user);



}
