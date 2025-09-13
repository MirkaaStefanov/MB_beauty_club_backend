package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.enums.AppointmentStatus;
import com.example.MB_beauty_club_backend.models.entity.Appointment;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByWorker(Worker worker);

    List<Appointment> findByUser(User user);

    List<Appointment> findByWorkerAndStatus(Worker worker, AppointmentStatus status);
}
