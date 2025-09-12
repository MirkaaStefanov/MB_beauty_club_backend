package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.Product;
import jakarta.persistence.Column;
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
public class OrderProductDTO {

    private Long id;
    private Integer number;
    private ProductDTO product;
    private OrderDTO order;
    private boolean deleted;
    private BigDecimal sellingPrice;

}
