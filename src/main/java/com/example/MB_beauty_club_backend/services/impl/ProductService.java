package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.ProductCategory;
import com.example.MB_beauty_club_backend.models.dto.ProductDTO;
import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ModelMapper mapper;

    public List<ProductDTO> findAll(Boolean forSale,ProductCategory category) {
        List<Product> items = repository.findByDeletedFalse();

        return items.stream()
                .filter(item -> (forSale == null || item.isForSale() == forSale) &&
                        (category == null || item.getProductCategory() == category))
                .map(item -> {
                    ProductDTO dto = mapper.map(item, ProductDTO.class);
                    dto.setImage(item.getBase64Image());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) {
        Product item = repository.findByIdAndDeletedFalse(id).orElseThrow();
        ProductDTO dto = mapper.map(item, ProductDTO.class);
        dto.setImage(item.getBase64Image());
        return dto;
    }

    public ProductDTO save(ProductDTO dto) {
        Product entity = mapper.map(dto, Product.class);
        BigDecimal euroPrice = entity.getPrice().divide(new BigDecimal("1.95583"), 2, RoundingMode.HALF_UP);
        entity.setEuroPrice(euroPrice);
        if (dto.getImage() != null) {
            entity.setImageData(Base64.getDecoder().decode(dto.getImage()));
        }
        return mapper.map(repository.save(entity), ProductDTO.class);
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = repository.findByIdAndDeletedFalse(id).orElseThrow();
        BigDecimal euroPrice = existing.getPrice().divide(new BigDecimal("1.95583"), 2, RoundingMode.HALF_UP);
        existing.setEuroPrice(euroPrice);
        mapper.map(dto, existing);
        if (dto.getImage() != null) {
            existing.setImageData(Base64.getDecoder().decode(dto.getImage()));
        }
        return mapper.map(repository.save(existing), ProductDTO.class);
    }

    public void delete(Long id) throws ChangeSetPersister.NotFoundException {
        Product existing = repository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        existing.setDeleted(true);
        repository.save(existing);
    }

    public ProductDTO toggleAvailability(Long id) throws ChangeSetPersister.NotFoundException {
        Product item = repository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        item.setForSale(!item.isForSale());
        return mapper.map(repository.save(item), ProductDTO.class);
    }
}
