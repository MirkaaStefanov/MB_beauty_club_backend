package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.dto.WorkerDTO;
import com.example.MB_beauty_club_backend.services.impl.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
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
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping
    public ResponseEntity<WorkerDTO> create(@RequestParam UUID userId, @RequestParam WorkerCategory category, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(workerService.createWorker(userId, category));
    }

    @GetMapping
    public ResponseEntity<List<WorkerDTO>> all(@RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(workerService.allWorkers());
    }

    @PutMapping
    public ResponseEntity<WorkerDTO> update(@RequestParam UUID id, @RequestBody WorkerDTO workerDTO, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(workerService.update(id, workerDTO));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam UUID id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        workerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerDTO> findById(@PathVariable UUID id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(workerService.findByid(id));
    }

    @GetMapping("/byCategory")
    public ResponseEntity<List<WorkerDTO>> findById(@RequestParam WorkerCategory category, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(workerService.findByCategory(category));
    }


}
