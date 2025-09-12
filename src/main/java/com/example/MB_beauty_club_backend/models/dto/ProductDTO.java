package com.example.MB_beauty_club_backend.models.dto;

import com.example.MB_beauty_club_backend.enums.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private BigDecimal price;
    private int availableQuantity;
    private boolean deleted;
    private String name;
    private String description;
    private boolean forSale;
    private String barcode;
    private ProductCategory productCategory;
    private String image;
    @JsonIgnore
    private transient MultipartFile imageFile;

}
