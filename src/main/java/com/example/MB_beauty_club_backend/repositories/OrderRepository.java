package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeletedFalse();

    Order findFirstByDeletedFalseOrderByIdDesc();

    Optional<Order> findByIdAndDeletedFalse(Long id);
    List<Order> findAllByUserAndDeletedFalse(User user);



}
