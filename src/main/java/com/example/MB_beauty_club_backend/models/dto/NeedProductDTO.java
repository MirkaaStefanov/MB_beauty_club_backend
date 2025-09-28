package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.models.entity.Product;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NeedProductDTO {

    private Long id;
    private ProductDTO product;
    private int quantity;


}
