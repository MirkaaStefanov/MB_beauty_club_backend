package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private Long id;
    private ProductDTO product;
    private int quantity;
    private BigDecimal price;
    private boolean deleted;
    private ShoppingCartDTO shoppingCart;
}
