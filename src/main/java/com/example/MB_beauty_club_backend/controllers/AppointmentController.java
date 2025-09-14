package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.enums.AppointmentStatus;
import com.example.MB_beauty_club_backend.models.dto.AppointmentDTO;
import com.example.MB_beauty_club_backend.services.impl.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentDTO>> getMyAppointments(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(appointmentService.findByUser());
    }

    @GetMapping("/worker-appointments/{id}")
    public ResponseEntity<List<AppointmentDTO>> getWorkerAppointments(@PathVariable Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(appointmentService.findByWorker(id));
    }

    @GetMapping("/pending-worker-appointments")
    public ResponseEntity<List<AppointmentDTO>> getPendingWorkerAppointments(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(appointmentService.findPendingByWorker());
    }

    @PostMapping("/book")
    public ResponseEntity<Void> bookAppointment(@RequestBody AppointmentDTO appointmentDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        appointmentService.save(appointmentDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(@PathVariable Long id, @RequestParam("status") AppointmentStatus status, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        AppointmentDTO updatedAppointment = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        appointmentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
