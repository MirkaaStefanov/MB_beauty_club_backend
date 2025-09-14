package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.models.dto.VacationDTO;
import com.example.MB_beauty_club_backend.services.impl.VacationService;
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
@RequestMapping("/api/v1/vacations")
@RequiredArgsConstructor
public class VacationsController {

    private final VacationService vacationService;

    @GetMapping
    public ResponseEntity<List<VacationDTO>> getMyVacations(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(vacationService.findByAuthenticatedWorker());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<VacationDTO>> getVacationsByWorkerId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(vacationService.findByWorkerId(id));
    }

    @PutMapping
    public ResponseEntity<Void> setVacation(@RequestBody VacationDTO newVacations, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        vacationService.save(newVacations);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        vacationService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}