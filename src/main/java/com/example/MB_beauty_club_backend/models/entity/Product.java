package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.ProductCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private BigDecimal price;
    private BigDecimal euroPrice;
    @Column(name = "available_quantity")
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private int availableQuantity;
    @Column(name = "is_deleted")
    private boolean deleted = false;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "for_sale")
    private boolean forSale;
    @Column(name = "barcode")
    private String barcode;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;
    @Column(name = "is_promoted")
    private boolean promotion = false;
    @Column(name = "percent")
    private int percent;
    private BigDecimal promotionPrice;
    private BigDecimal promotionEuroPrice;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", nullable = false)
    private byte[] imageData;

    public String getBase64Image() {
        if (this.imageData == null) {
            return null;
        }
        return java.util.Base64.getEncoder().encodeToString(this.imageData);
    }

}
