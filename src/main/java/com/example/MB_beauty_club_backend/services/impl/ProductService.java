package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.ProductCategory;
import com.example.MB_beauty_club_backend.models.dto.ProductDTO;
import com.example.MB_beauty_club_backend.models.entity.CartItem;
import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.repositories.CartItemRepository;
import com.example.MB_beauty_club_backend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper mapper;
    private static final BigDecimal EURO_EXCHANGE_RATE = new BigDecimal("1.95583");


    public List<ProductDTO> findAll(Boolean forSale, ProductCategory category) {
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
        BigDecimal euroPrice = entity.getPrice().divide(EURO_EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        entity.setEuroPrice(euroPrice);
        if (dto.getImage() != null) {
            entity.setImageData(Base64.getDecoder().decode(dto.getImage()));
        }
        return mapper.map(repository.save(entity), ProductDTO.class);
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Product updated = mapper.map(dto, Product.class);

        BigDecimal euroPrice = dto.getPrice().divide(EURO_EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        updated.setEuroPrice(euroPrice);


        if (dto.getImage() != null) {
            updated.setImageData(Base64.getDecoder().decode(dto.getImage()));
        }
        updated.setId(id);
        return mapper.map(repository.save(updated), ProductDTO.class);
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

    @Transactional
    public ProductDTO createPromotion(Long id, int percent) throws ChangeSetPersister.NotFoundException {
        Product item = repository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Promotion percentage must be between 0 and 100.");
        }

        // Calculate the discount amount.
        BigDecimal discountPercent = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = item.getPrice().multiply(discountPercent);

        // Calculate the new promotional price in both BGN and EUR.
        BigDecimal newPrice = item.getPrice().subtract(discountAmount);
        BigDecimal newEuroPrice = newPrice.divide(EURO_EXCHANGE_RATE, 2, RoundingMode.HALF_UP);

        item.setPromotion(true);
        item.setPercent(percent);
        item.setPromotionPrice(newPrice);
        item.setPromotionEuroPrice(newEuroPrice);


        List<CartItem> cartItemList = cartItemRepository.findByProductAndDeletedFalse(item);

        for (CartItem cartItem : cartItemList){
            cartItem.setPrice(item.getPromotionPrice());
            cartItem.setEuroPrice(item.getEuroPrice());
            cartItemRepository.save(cartItem);
        }


        return mapper.map(repository.save(item), ProductDTO.class);
    }

    @Transactional
    public ProductDTO deletePromotion(Long id) throws ChangeSetPersister.NotFoundException {
        Product item = repository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<CartItem> cartItemList = cartItemRepository.findByProductAndDeletedFalse(item);

        for (CartItem cartItem : cartItemList){
            cartItem.setPrice(item.getPrice());
            cartItem.setEuroPrice(item.getEuroPrice());
            cartItemRepository.save(cartItem);
        }

        item.setPromotion(false);
        item.setPercent(0);
        item.setPromotionPrice(null);
        item.setPromotionEuroPrice(null);

        return mapper.map(repository.save(item), ProductDTO.class);
    }

}
