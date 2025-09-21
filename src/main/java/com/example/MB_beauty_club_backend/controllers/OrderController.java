package com.example.MB_beauty_club_backend.controllers;

import com.example.MB_beauty_club_backend.exceptions.InsufficientStockException;
import com.example.MB_beauty_club_backend.models.dto.OrderDTO;
import com.example.MB_beauty_club_backend.models.dto.OrderProductDTO;
import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.services.impl.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders( @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<OrderProductDTO>> findOrderProductsForOrder(@PathVariable UUID id,  @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.findOrderProductsForOrder(id));
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestHeader("Authorization") String auth) throws InsufficientStockException, ChangeSetPersister.NotFoundException {
        orderService.createOrder();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable UUID id, @RequestBody OrderDTO orderDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/last")
    public ResponseEntity<OrderDTO> findFirstByOrderByIdDesc(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(orderService.findFirstByOrderByIdDesc());
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getAllOrdersForAuthenticatedUser(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.getAllOrdersForAuthenticatedUser());
    }
}
