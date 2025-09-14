package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private WorkerCategory workerCategory;
    @Column(name = "is_deleted")
    private boolean deleted = false;


}
