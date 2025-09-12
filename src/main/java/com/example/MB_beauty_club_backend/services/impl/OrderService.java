package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.dto.OrderDTO;
import com.example.MB_beauty_club_backend.models.dto.OrderProductDTO;
import com.example.MB_beauty_club_backend.models.dto.ProductDTO;
import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.models.entity.CartItem;
import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.repositories.CartItemRepository;
import com.example.MB_beauty_club_backend.repositories.OrderRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final OrderProductService orderProductService;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findByDeletedFalse();
        return orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).toList();
    }

    public OrderDTO getOrderById(Long id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(order, OrderDTO.class);
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

    public OrderDTO createOrder(OrderDTO orderDTO) {
        validateOrderDTO(orderDTO);
        orderDTO.setOrderDate(orderDTO.getOrderDate());
        Order order = orderRepository.save(modelMapper.map(orderDTO, Order.class));
        return modelMapper.map(order, OrderDTO.class);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) throws ChangeSetPersister.NotFoundException {
        validateOrderDTO(orderDTO);
        Order existingOrder = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Order updatedOrder = modelMapper.map(orderDTO, Order.class);
        updatedOrder.setId(existingOrder.getId());
        updatedOrder.setOrderDate(orderDTO.getOrderDate());
        orderRepository.save(updatedOrder);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    public void deleteOrder(Long id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        order.setDeleted(true);
        order.setUser(null);
        orderRepository.delete(order);
    }

    public OrderDTO findFirstByOrderByIdDesc() {
        return modelMapper.map(orderRepository.findFirstByDeletedFalseOrderByIdDesc(), OrderDTO.class);
    }

    public List<Order> getAllOrdersForAuthenticatedUser() throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        return orderRepository.findAllByUserAndDeletedFalse(authenticatedUser);
    }

    public void createOrderFromShoppingCart() throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        PublicUserDTO userDTO = modelMapper.map(authenticatedUser, PublicUserDTO.class);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate(LocalDate.now());
        orderDTO.setUser(userDTO);
        OrderDTO order = createOrder(orderDTO);

        if (cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart).isEmpty()) {
            throw new ValidationException("Shopping cart is empty!");
        }

        for (CartItem cartItem : cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart)) {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setOrder(order);
            orderProductDTO.setNumber(cartItem.getQuantity());
            orderProductDTO.setProduct(modelMapper.map(cartItem.getProduct(), ProductDTO.class));

            //reduce available quantity
            cartItem.getProduct().setAvailableQuantity(cartItem.getProduct().getAvailableQuantity() - cartItem.getQuantity());
            productRepository.save(cartItem.getProduct());

            orderProductDTO.setSellingPrice(cartItem.getProduct().getPrice());
            orderProductService.createOrderProduct(orderProductDTO);
        }

        cartItemRepository.deleteAll(cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart));

    }


}
