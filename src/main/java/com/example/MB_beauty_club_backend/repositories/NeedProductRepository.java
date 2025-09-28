package com.example.MB_beauty_club_backend.repositories;

import com.example.MB_beauty_club_backend.models.dto.NeedProductDTO;
import com.example.MB_beauty_club_backend.models.entity.NeedProduct;
import com.example.MB_beauty_club_backend.models.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NeedProductRepository extends JpaRepository<NeedProduct, Long> {
    Optional<NeedProduct> findByProduct(Product product);

}
