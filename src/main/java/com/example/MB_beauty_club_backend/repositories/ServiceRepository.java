package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.entity.Service;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    List<Service> findAllByDeletedFalse();

    List<Service> findByCategoryAndDeletedFalse(WorkerCategory category);
}
