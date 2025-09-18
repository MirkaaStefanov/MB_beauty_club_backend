package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.OrderStatus;
import com.example.MB_beauty_club_backend.models.dto.CartItemDTO;
import com.example.MB_beauty_club_backend.models.dto.OrderDTO;
import com.example.MB_beauty_club_backend.models.dto.OrderProductDTO;
import com.example.MB_beauty_club_backend.models.entity.CartItem;
import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.OrderProduct;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.repositories.CartItemRepository;
import com.example.MB_beauty_club_backend.repositories.OrderProductRepository;
import com.example.MB_beauty_club_backend.repositories.OrderRepository;
import com.example.MB_beauty_club_backend.repositories.ShoppingCartRepository;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findByDeletedFalse();
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    public OrderDTO getOrderById(UUID id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(order, OrderDTO.class);
    }

    public List<OrderProductDTO> findOrderProductsForOrder(UUID id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderAndDeletedFalse(order);

        return orderProducts.stream()
                .map(orderProduct -> modelMapper.map(orderProduct, OrderProductDTO.class)) // Map each CartItem to CartItemDTO
                .toList();
    }

    void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO.getUser() != null) {
            boolean orderExists = userRepository.existsById(orderDTO.getUser().getId());
            if (!orderExists) {
                throw new ValidationException("Client does not exist with ID: " + orderDTO.getUser().getId());
            }
        } else {
            throw new ValidationException("Client ID cannot be null");
        }
    }

    @Transactional
    public void createOrder() throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<CartItem> cartItemList = cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart);

        Order order = new Order();
        order.setUser(authenticatedUser);
        order.setOrderDate(LocalDate.now());
        if (authenticatedUser.getRole().equals("ADMIN")) {
            order.setStatus(OrderStatus.DONE);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }

        BigDecimal totalLeva = cartItemList.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEuro = cartItemList.stream()
                .map(item -> item.getEuroPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setPrice(totalLeva);
        order.setEuroPrice(totalEuro);

        orderRepository.save(order);

        for (CartItem cartItem : cartItemList) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setUser(authenticatedUser);
            orderProduct.setProduct(cartItem.getProduct());
            orderProduct.setQuantity(cartItem.getQuantity());
            orderProduct.setPrice(cartItem.getPrice());
            orderProduct.setEuroPrice(cartItem.getEuroPrice());
            orderProductRepository.save(orderProduct);
        }



        cartItemRepository.deleteAll(cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart));

    }

    public OrderDTO updateOrder(UUID id, OrderDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        validateOrderDTO(orderDTO);
        Order existingOrder = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Order updatedOrder = modelMapper.map(orderDTO, Order.class);
        updatedOrder.setId(existingOrder.getId());
        updatedOrder.setOrderDate(orderDTO.getOrderDate());
        orderRepository.save(updatedOrder);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    public void deleteOrder(UUID id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        order.setUser(null);
        orderRepository.save(order);
    }

    public OrderDTO findFirstByOrderByIdDesc() {
        return modelMapper.map(orderRepository.findFirstByDeletedFalseOrderByIdDesc(), OrderDTO.class);
    }

    public List<OrderDTO> getAllOrdersForAuthenticatedUser() throws ChangeSetPersister.NotFoundException {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<Order> orders = orderRepository.findAllByUserAndDeletedFalse(authenticatedUser);

        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

}
