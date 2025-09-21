package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
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
    private int quantity;
    private ProductDTO product;
    private OrderDTO order;
    private PublicUserDTO user;
    private boolean deleted;
    private BigDecimal price;
    private BigDecimal euroPrice;

}
