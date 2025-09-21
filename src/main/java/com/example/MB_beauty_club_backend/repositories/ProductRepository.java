package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedFalse();

    Optional<Product> findByIdAndDeletedFalse(Long id);

    Optional<Product> findByBarcodeAndDeletedFalse(String barcode);

    @Query("SELECT p FROM Product p WHERE p.forSale=true")
    List<Product> getProductsForSale();

    @Query("SELECT p FROM Product p WHERE p.availableQuantity > 0")
    List<Product> getAvailableProducts();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = ?1 AND p.deleted = false")
    Optional<Product> findByIdWithLock(Long id);
}
