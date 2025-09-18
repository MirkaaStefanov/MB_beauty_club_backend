package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.models.dto.CartItemDTO;
import com.example.MB_beauty_club_backend.services.impl.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping("/addToCart")
    public void addToCart(@RequestParam(value = "productId") Long productId, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth) throws Exception {
        shoppingCartService.addToCart(productId, quantity);
    }

    @PostMapping("/addToCartByBarcode")
    public void addToCartByBarcode(@RequestParam(value = "barcode") String barcode, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth) throws Exception {
        shoppingCartService.addToCartByBarcode(barcode, quantity);
    }

    @GetMapping("/showCart")
    public ResponseEntity<List<CartItemDTO>> showCart(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(shoppingCartService.showCart());
    }

    @PostMapping("/removeCartItem")
    public void removeCartItem(@RequestParam("cartItemId") Long cartItemId, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        shoppingCartService.removeCartItem(cartItemId);
    }

    @PutMapping("/updateQuantity")
    private void updateQuantity(@RequestParam("cartItemId") Long cartItemId, @RequestParam int quantity, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        shoppingCartService.updateQuantityOfItem(cartItemId,quantity);
    }

}
