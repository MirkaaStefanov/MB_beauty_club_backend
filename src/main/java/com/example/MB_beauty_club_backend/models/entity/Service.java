package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private WorkerCategory category;

    private String name;
    private String description;
    private Double price;
    private Integer duration;
    @Column(name = "is_deleted")
    private boolean deleted;
}
