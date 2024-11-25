package com.example.commerce.service;

import com.example.commerce.dto.*;
import com.example.commerce.model.*;
import com.example.commerce.repository.CartRepository;
import com.example.commerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.commerce.converters.CartConventer;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartConventer cartConventer;
    private final ProductService productService;  // Inject ProductService
    private final CustomerService customerService;  // Inject CustomerService

    // Constructor Injection for all dependencies
    public CartService(CartRepository cartRepository,
                       OrderRepository orderRepository,
                       CartConventer cartConventer,
                       ProductService productService,
                       CustomerService customerService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartConventer = cartConventer;
        this.productService = productService;
        this.customerService = customerService;
    }

    // Add product to the cart
    public CartDto addProductToCart(Long customerId, Long productId, int quantity) {
        CustomerDto customerDto = customerService.getCustomerById(customerId);
        ProductDto productDto = productService.getProduct(productId);

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer."));

        // Create a new cart item and add it to the cart
        CartItem newItem = new CartItem();
        newItem.setProduct(new Product(productDto));
        newItem.setQuantity(quantity);
        newItem.setPriceAtTimeOfAdd(productDto.getPrice());

        cart.getItems().add(newItem);

        // Recalculate total price of the cart
        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtTimeOfAdd() * item.getQuantity())
                .sum());

        cartRepository.save(cart);

        return cartConventer.convertCartToDto(cart);
    }

    // Delete a product from the cart
    public CartDto deleteProductFromCart(Long customerId, Long productId) {
        CustomerDto customerDto = customerService.getCustomerById(customerId);

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer."));

        // Remove the product from the cart
        cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));

        // Recalculate total price
        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtTimeOfAdd() * item.getQuantity())
                .sum());

        cartRepository.save(cart);

        return cartConventer.convertCartToDto(cart);
    }

    // Update the quantity of a product in the cart
    public CartDto updateCart(Long customerId, Long productId, int quantity) {
        CustomerDto customerDto = customerService.getCustomerById(customerId);
        ProductDto productDto = productService.getProduct(productId);

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer."));

        // Update the cart item quantity
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getId().equals(productId)) {
                cartItem.setQuantity(quantity);
                cartItem.setPriceAtTimeOfAdd(productDto.getPrice());
                break;
            }
        }

        // Recalculate total price
        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtTimeOfAdd() * item.getQuantity())
                .sum());

        cartRepository.save(cart);

        return cartConventer.convertCartToDto(cart);
    }

    // Empty the cart
    public CartDto emptyCart(Long customerId) {
        CustomerDto customerDto = customerService.getCustomerById(customerId);

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer."));

        // Clear the items in the cart
        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);

        return cartConventer.convertCartToDto(cart);
    }

    // Place an order
    @Transactional
    public OrderDto placeOrder(Long customerId) {
        CustomerDto customerDto = customerService.getCustomerById(customerId);

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("The cart is empty.");
        }

        Order order = new Order();
        order.setCustomer(new Customer(customerDto));
        double orderTotal = 0.0;
        Set<OrderItem> orderItems = new HashSet<>();

        // Add items to the order
        for (CartItem cartItem : cart.getItems()) {
            ProductDto productDto = productService.getProduct(cartItem.getProduct().getId());
            int quantityToOrder = cartItem.getQuantity();

            if (productDto.getStock() < quantityToOrder) {
                throw new IllegalArgumentException("Not enough stock for product: " + productDto.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(new Product(productDto));
            orderItem.setQuantity(quantityToOrder);
            orderItem.setPriceAtTimeOfPurchase(cartItem.getPriceAtTimeOfAdd());
            orderItems.add(orderItem);

            orderTotal += cartItem.getPriceAtTimeOfAdd() * quantityToOrder;
        }

        order.setItems(orderItems);
        order.setTotalPrice(orderTotal);
        order.setOrderStatus("PLACED");

        orderRepository.save(order);

        // Clear the cart after placing the order
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        // Convert Order to OrderDto
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCustomerId(order.getCustomer().getId());
        orderDto.setStatus(order.getOrderStatus());
        orderDto.setTotalPrice(order.getTotalPrice());

        Set<OrderItemDto> orderItemDtos = order.getItems().stream().map(orderItem -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(orderItem.getProduct().getId());
            itemDto.setQuantity(orderItem.getQuantity());
            itemDto.setPriceAtTimeOfPurchase(orderItem.getPriceAtTimeOfPurchase());
            return itemDto;
        }).collect(Collectors.toSet());

        orderDto.setItems(orderItemDtos);

        return orderDto;
    }

    // Get cart by customerId
    public CartDto getCartByCustomerId(Long customerId) {
        customerService.getCustomerById(customerId);

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer."));
        return cartConventer.convertCartToDto(cart);
    }
}
