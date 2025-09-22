package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByDeletedFalse();

    Optional<OrderProduct> findByIdAndDeletedFalse(Long id);
    List<OrderProduct> findAllByOrderAndDeletedFalse(Order order);

}
