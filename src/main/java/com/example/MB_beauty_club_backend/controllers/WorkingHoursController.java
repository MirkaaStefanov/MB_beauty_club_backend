package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.models.dto.WorkingHoursDTO;
import com.example.MB_beauty_club_backend.services.impl.WorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/working-hours")
@RequiredArgsConstructor
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @GetMapping
    public ResponseEntity<List<WorkingHoursDTO>> getWorkingHours(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(workingHoursService.findByWorkerId());
    }

    @PutMapping
    public ResponseEntity<Void> setWorkingHours(@RequestBody List<WorkingHoursDTO> newHours, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        workingHoursService.setWorkingHoursForWorker(newHours);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        workingHoursService.delete(id);
        return ResponseEntity.ok().build();
    }
}

