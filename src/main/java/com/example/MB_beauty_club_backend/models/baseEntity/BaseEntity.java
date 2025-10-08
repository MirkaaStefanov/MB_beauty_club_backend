package com.example.MB_beauty_club_backend.models.baseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID") // 1. Tells JPA to use a custom generator
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator") // 2. Defines the generator as Hibernate's UUID generator
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.deletedAt = null;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}