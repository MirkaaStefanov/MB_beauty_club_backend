package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private WorkerCategory category;

    private String name;
    private String description;
    private Double price;
    private Integer duration;
    @Column(name = "is_deleted")
    private boolean deleted = false;
}
