package com.example.MB_beauty_club_backend.controllers;


import com.example.MB_beauty_club_backend.models.dto.ServiceDTO;
import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.services.impl.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServicesController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices(@RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(serviceService.findAll());
    }

    @GetMapping("/byCategory")
    public ResponseEntity<List<ServiceDTO>> getServicesByCategory(@RequestParam("category") WorkerCategory category, @RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(serviceService.findByCategory(category));
    }

    @GetMapping("/byId")
    public ResponseEntity<ServiceDTO> getServiceById(@RequestParam("id") Long id, @RequestHeader(value = "Authorization", required = false) String auth) {
        ServiceDTO service = serviceService.findById(id);
        if (service != null) {
            return ResponseEntity.ok(service);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createService(@RequestBody ServiceDTO serviceDTO, @RequestHeader("Authorization") String auth) {
        serviceService.save(serviceDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateService(@RequestBody ServiceDTO serviceDTO, @RequestHeader("Authorization") String auth) {
        serviceService.save(serviceDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteService(@RequestParam("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        serviceService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
