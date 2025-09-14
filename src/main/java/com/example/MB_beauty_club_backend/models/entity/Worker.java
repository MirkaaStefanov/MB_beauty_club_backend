package com.example.MB_beauty_club_backend.models.entity;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.lang.reflect.Type;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;
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
