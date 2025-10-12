package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.enums.ProductCategory;
import com.example.MB_beauty_club_backend.models.dto.NeedProductDTO;
import com.example.MB_beauty_club_backend.models.dto.ProductDTO;
import com.example.MB_beauty_club_backend.services.impl.DatabaseBackupService;
import com.example.MB_beauty_club_backend.services.impl.MailService;
import com.example.MB_beauty_club_backend.services.impl.ProductService;
import jakarta.mail.MessagingException;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final MailService mailService;
    private final DatabaseBackupService databaseBackupService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(@RequestParam(required = false) Boolean forSale, @RequestParam(required = false) ProductCategory category, @RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(productService.findAll(forSale, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto, @RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(productService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO dto, @RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<ProductDTO> toggleAvailability(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(productService.toggleAvailability(id));
    }

    @PutMapping("/{id}/promote/{percent}")
    public ResponseEntity<ProductDTO> createPromotion(@PathVariable Long id, @PathVariable int percent, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {

        return ResponseEntity.ok(productService.createPromotion(id, percent));
    }

    @DeleteMapping("/{id}/promote")
    public ResponseEntity<ProductDTO> deletePromotion(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {

        return ResponseEntity.ok(productService.deletePromotion(id));
    }

    @PutMapping("/{id}/restock/{quantity}")
    public ResponseEntity<ProductDTO> restock(@PathVariable Long id, @PathVariable int quantity, @RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(productService.restock(id, quantity));
    }

    @PostMapping("/export-database")
    public ResponseEntity<Void> exportDatabase(@RequestHeader(value = "Authorization", required = false) String auth) throws ChangeSetPersister.NotFoundException, IOException, InterruptedException, MessagingException {

        File backupFile = databaseBackupService.exportDatabase();
        mailService.sendDatabaseBackup(backupFile);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/need-products")
    public ResponseEntity<List<NeedProductDTO>> getAllNeedProducts(@RequestHeader(value = "Authorization") String auth){
        return ResponseEntity.ok(productService.getAllNeedProducts());
    }


}
