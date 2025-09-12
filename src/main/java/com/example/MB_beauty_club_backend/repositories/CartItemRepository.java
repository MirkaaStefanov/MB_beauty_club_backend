package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.CartItem;
import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndDeletedFalse(Long id);
    List<CartItem> findByShoppingCartAndDeletedFalse(ShoppingCart shoppingCart);
    Optional<CartItem> findByProductAndShoppingCartAndDeletedFalse(Product product, ShoppingCart shoppingCart);
}
