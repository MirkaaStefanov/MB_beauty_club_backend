package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.dto.OrderProductDTO;
import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.OrderProduct;
import com.example.MB_beauty_club_backend.repositories.OrderProductRepository;
import com.example.MB_beauty_club_backend.repositories.OrderRepository;
import com.example.MB_beauty_club_backend.repositories.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<OrderProductDTO> getAllOrderProducts() {
        List<OrderProduct> orderProducts = orderProductRepository.findByDeletedFalse();
        return orderProducts.stream()
                .map(orderProduct -> modelMapper.map(orderProduct, OrderProductDTO.class))
                .toList();
    }

    public OrderProductDTO getOrderProductById(Long id) throws ChangeSetPersister.NotFoundException {
        OrderProduct order = orderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(order, OrderProductDTO.class);
    }

    void validateOrderProductDTO(OrderProductDTO orderProductDTO) {
        if (orderProductDTO.getOrder() != null) {
            boolean orderExists = orderRepository.existsById(orderProductDTO.getOrder().getId());
            if (!orderExists) {
                throw new ValidationException("Order does not exist with ID: " + orderProductDTO.getOrder().getId());
            }
        } else {
            throw new ValidationException("Order ID cannot be null!");
        }
    }

    public OrderProductDTO createOrderProduct(OrderProductDTO orderDTO) {
        validateOrderProductDTO(orderDTO);
        OrderProduct order = orderProductRepository.save(modelMapper.map(orderDTO, OrderProduct.class));
        return modelMapper.map(order, OrderProductDTO.class);
    }

    public OrderProductDTO updateOrderProduct(Long id, OrderProductDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        validateOrderProductDTO(orderDTO);

        OrderProduct existingOrderProduct = orderProductRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        OrderProduct updatedOrderProduct = modelMapper.map(orderDTO, OrderProduct.class);
        updatedOrderProduct.setId(existingOrderProduct.getId());
        orderProductRepository.save(updatedOrderProduct);
        return modelMapper.map(updatedOrderProduct, OrderProductDTO.class);
    }

    public void deleteOrderProduct(Long id) throws ChangeSetPersister.NotFoundException {
        OrderProduct order = orderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        orderProductRepository.save(order);
    }



    public List<OrderProductDTO> getOrderProducts(Long orderId) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(order);
        return orderProducts.stream()
                .map(orderProduct -> modelMapper.map(orderProduct, OrderProductDTO.class))
                .collect(Collectors.toList());
    }

}
