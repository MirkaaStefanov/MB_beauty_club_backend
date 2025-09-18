package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDate orderDate;
    @Column(name = "is_deleted")
    private boolean deleted = false;
    @Column(name = "is_invoiced")
    private boolean invoiced;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal price;
    private BigDecimal euroPrice;
}
