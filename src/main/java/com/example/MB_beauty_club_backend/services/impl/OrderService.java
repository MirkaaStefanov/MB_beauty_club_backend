package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.OrderStatus;
import com.example.MB_beauty_club_backend.enums.Role;
import com.example.MB_beauty_club_backend.exceptions.InsufficientStockException;
import com.example.MB_beauty_club_backend.models.dto.CartItemDTO;
import com.example.MB_beauty_club_backend.models.dto.OrderDTO;
import com.example.MB_beauty_club_backend.models.dto.OrderProductDTO;
import com.example.MB_beauty_club_backend.models.entity.CartItem;
import com.example.MB_beauty_club_backend.models.entity.NeedProduct;
import com.example.MB_beauty_club_backend.models.entity.Order;
import com.example.MB_beauty_club_backend.models.entity.OrderProduct;
import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.repositories.CartItemRepository;
import com.example.MB_beauty_club_backend.repositories.NeedProductRepository;
import com.example.MB_beauty_club_backend.repositories.OrderProductRepository;
import com.example.MB_beauty_club_backend.repositories.OrderRepository;
import com.example.MB_beauty_club_backend.repositories.ProductRepository;
import com.example.MB_beauty_club_backend.repositories.ShoppingCartRepository;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final MailService mailService;
    private final NeedProductRepository needProductRepository;

    @Value("${spring.security.mail.admin}")
    private String adminEmail;

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
    public OrderDTO createOrder() throws InsufficientStockException, ChangeSetPersister.NotFoundException, MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<CartItem> cartItemList = cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart);

        if (cartItemList.isEmpty()) {
            throw new InsufficientStockException("Your shopping cart is empty.");
        }

       if(!authenticatedUser.getRole().equals(Role.ADMIN)){
           String validationMessage = shoppingCartService.validateAndCorrectCartItems(cartItemList);
           if (!validationMessage.isEmpty()) {
               throw new InsufficientStockException(validationMessage);
           }
       }


        // After validation, re-fetch the possibly modified cart items
        List<CartItem> finalCartItemList = cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart);

        // Sort cart items by product ID to ensure deterministic locking and stock reduction
        finalCartItemList.sort(Comparator.comparing(item -> item.getProduct().getId()));

        // Reduce stock for confirmed items
        for (CartItem cartItem : finalCartItemList) {
            Product product = productRepository.findByIdWithLock(cartItem.getProduct().getId())
                    .orElseThrow(() -> new InsufficientStockException("Product not found: " + cartItem.getProduct().getId()));


            if(product.getAvailableQuantity() - cartItem.getQuantity() < 0){


                Optional<NeedProduct> optionalNeedProduct = needProductRepository.findByProduct(product);

                if(optionalNeedProduct.isPresent()){
                    int quantity = cartItem.getQuantity() - product.getAvailableQuantity();
                    NeedProduct needProduct = optionalNeedProduct.get();
                    needProduct.setQuantity(needProduct.getQuantity()+quantity);
                    needProductRepository.save(needProduct);
                }else{
                    NeedProduct needProduct = new NeedProduct();
                    needProduct.setProduct(product);
                    needProduct.setQuantity(cartItem.getQuantity() - product.getAvailableQuantity());
                    needProductRepository.save(needProduct);
                }
                product.setAvailableQuantity(0);
            }else{
                product.setAvailableQuantity(product.getAvailableQuantity() - cartItem.getQuantity());
            }


            productRepository.save(product);

        }

        // Now that stock is confirmed and reduced, create the order and order products
        Order order = new Order();
        order.setUser(authenticatedUser);
        order.setOrderDate(LocalDate.now());
        if (authenticatedUser.getRole().equals(Role.ADMIN)) {
            order.setStatus(OrderStatus.DONE);
        }

        order.setOrderNumber(generateUniqueOrderNumber());

        BigDecimal totalLeva = finalCartItemList.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEuro = finalCartItemList.stream()
                .map(item -> item.getEuroPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setPrice(totalLeva);
        order.setEuroPrice(totalEuro);

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : finalCartItemList) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setUser(authenticatedUser);
            orderProduct.setProduct(cartItem.getProduct());
            orderProduct.setQuantity(cartItem.getQuantity());
            orderProduct.setPrice(cartItem.getPrice());
            orderProduct.setEuroPrice(cartItem.getEuroPrice());

            orderProductRepository.save(orderProduct);
        }

        if(authenticatedUser.getRole().equals(Role.ADMIN)){
            cartItemRepository.deleteAll(cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart));
        }

        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Transactional
    public void cancelOrder(UUID orderId) throws ChangeSetPersister.NotFoundException {

        Order order = orderRepository.findByIdAndDeletedFalse(orderId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderAndDeletedFalse(order);


        for (OrderProduct orderProduct : orderProducts) {
            Product product = orderProduct.getProduct();
            product.setAvailableQuantity(product.getAvailableQuantity() + orderProduct.getQuantity());
            productRepository.save(product);

        }

        orderProductRepository.deleteAll(orderProducts);
        deleteOrder(order.getId());
    }

    @Transactional
    public void orderPaySuccess(UUID orderId) throws ChangeSetPersister.NotFoundException, MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);


        Order order = orderRepository.findByIdAndDeletedFalse(orderId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderAndDeletedFalse(order);

        List<Product> lowStockProducts = new ArrayList<>();

        for (OrderProduct orderProduct : orderProducts) {
            Product product = productRepository.findByIdWithLock(orderProduct.getProduct().getId())
                    .orElseThrow(() -> new InsufficientStockException("Product not found: " + orderProduct.getProduct().getId()));

            if (product.getAvailableQuantity() < 10) {
                lowStockProducts.add(product);
            }
        }

        if (!lowStockProducts.isEmpty()) {
            mailService.sendLowStockReport(lowStockProducts);
        }

        cartItemRepository.deleteAll(cartItemRepository.findByShoppingCartAndDeletedFalse(shoppingCart));
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
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


    private String generateUniqueOrderNumber() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        String orderNumber;

        do {
            orderNumber = String.valueOf(random.nextInt(max - min + 1) + min);
        } while (orderRepository.findByOrderNumberAndDeletedFalse(orderNumber).isPresent());

        return orderNumber;
    }

    public OrderDTO updateStatus(UUID id) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (order.getStatus().equals(OrderStatus.PENDING)) {
            order.setStatus(OrderStatus.DELIVER);
        } else if (order.getStatus().equals(OrderStatus.DELIVER)) {
            order.setStatus(OrderStatus.DONE);
        }

        return modelMapper.map(orderRepository.save(order), OrderDTO.class);

    }

}
