package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.dto.CartItemDTO;
import com.example.MB_beauty_club_backend.models.entity.CartItem;
import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.repositories.CartItemRepository;
import com.example.MB_beauty_club_backend.repositories.ProductRepository;
import com.example.MB_beauty_club_backend.repositories.ShoppingCartRepository;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    public void addToCart(Long productId, int quantity) throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        Product product = productRepository.findById(productId).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<CartItem> cartItemList = cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart);
        boolean ifProductIsInCart = false;


        Optional<CartItem> optionalCartItem = cartItemRepository.findByProductAndShoppingCartAndDeletedFalse(product, shoppingCart);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            if (cartItem.getQuantity() + quantity > product.getAvailableQuantity()) {
                throw new IllegalArgumentException("There is no that much quantity");
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
                ifProductIsInCart = true;
            }
        }

        if (ifProductIsInCart == false) {
            if (quantity > product.getAvailableQuantity()) {
                throw new IllegalArgumentException("There is no that much quantity");
            }
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            if (cartItem.getQuantity() < 0) {
                throw new ValidationException("Quantity must be more than 0");
            }
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cartItem.setShoppingCart(shoppingCart);
            cartItemRepository.save(cartItem);
        }
    }

    public List<CartItemDTO> showCart() throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<CartItemDTO> cartItemDTOs = cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart)
                .stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemDTO.class)) // Map each CartItem to CartItemDTO
                .toList();

        return cartItemDTOs;
    }

    public void removeCartItem(Long cartItemId) throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        CartItem cartItem = cartItemRepository.findByIdAndDeletedFalse(cartItemId).orElseThrow(ChangeSetPersister.NotFoundException::new);

        cartItem.setShoppingCart(null);
        cartItem.setDeleted(true);
        cartItemRepository.delete(cartItem);

    }

    public void updateQuantityOfItem(Long cartItemId, int quantity) throws ChangeSetPersister.NotFoundException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be more than 0");
        }
        CartItem cartItem = cartItemRepository.findByIdAndDeletedFalse(cartItemId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (quantity > cartItem.getProduct().getAvailableQuantity()) {
            throw new IllegalArgumentException("There is no that much quantity");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

}
