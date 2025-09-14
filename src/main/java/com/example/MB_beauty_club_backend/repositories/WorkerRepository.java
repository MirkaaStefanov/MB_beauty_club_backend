package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.ls.LSException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    List<Worker> findAllByDeletedFalse();

    Optional<Worker> findByUserAndDeletedFalse(User user);

    List<Worker> findByWorkerCategoryAndDeletedFalse(WorkerCategory workerCategory);
}
