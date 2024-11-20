package com.example.commerce.service;

import com.example.commerce.dto.CartDto;
import com.example.commerce.dto.CartItemDto;
import com.example.commerce.dto.OrderDto;
import com.example.commerce.dto.OrderItemDto;
import com.example.commerce.model.*;
import com.example.commerce.repository.CartRepository;
import com.example.commerce.repository.CustomerRepository;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.commerce.converters.CartConventer;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartConventer cartConventer;

    public CartDto addProductToCart(Long customerId, Long productId, int quantity) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (customerOpt.isEmpty() || productOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer or Product not found.");
        }

        Customer customer = customerOpt.get();
        Product product = productOpt.get();

        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
        }

        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setPriceAtTimeOfAdd(product.getPrice());

        cart.getItems().add(newItem);

        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtTimeOfAdd() * item.getQuantity())
                .sum());

        cartRepository.save(cart);

        // Convert the Cart entity to a DTO and return
        return cartConventer.convertCartToDto(cart);
    }

    public CartDto deleteProductFromCart(Long customerId, Long productId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = customerOpt.get();
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for customer.");
        }

        cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));

        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtTimeOfAdd() * item.getQuantity())
                .sum());

        cartRepository.save(cart);

        // Convert the Cart entity to a DTO and return
        return cartConventer.convertCartToDto(cart);
    }

    public CartDto updateCart(Long customerId, Long productId, int quantity) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (customerOpt.isEmpty() || productOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer or Product not found.");
        }

        Customer customer = customerOpt.get();
        Product product = productOpt.get();
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found.");
        }

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getId().equals(productId)) {
                cartItem.setQuantity(quantity);
                cartItem.setPriceAtTimeOfAdd(product.getPrice());
                break;
            }
        }

        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtTimeOfAdd() * item.getQuantity())
                .sum());

        cartRepository.save(cart);

        // Convert the Cart entity to a DTO and return
        return cartConventer.convertCartToDto(cart);
    }

    public CartDto emptyCart(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = customerOpt.get();
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found.");
        }

        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);

        // Convert the Cart entity to a DTO and return
        return cartConventer.convertCartToDto(cart);
    }

    @Transactional
    public OrderDto placeOrder(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = customerOpt.get();
        Cart cart = customer.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("The cart is empty.");
        }

        Order order = new Order();
        order.setCustomer(customer);

        double orderTotal = 0.0;
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantityToOrder = cartItem.getQuantity();

            if (product.getStock() < quantityToOrder) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantityToOrder);
            orderItem.setPriceAtTimeOfPurchase(cartItem.getPriceAtTimeOfAdd());
            product.setStock(product.getStock() - quantityToOrder);
            orderItems.add(orderItem);

            orderTotal += cartItem.getPriceAtTimeOfAdd() * quantityToOrder;
        }

        order.setItems(orderItems);
        order.setTotalPrice(orderTotal);
        order.setOrderStatus("PLACED");

        orderRepository.save(order);

        // Clear cart after placing the order
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        // Map Order to OrderDto
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCustomerId(order.getCustomer().getId()); // Assuming Customer has getId() method
        orderDto.setStatus(order.getOrderStatus());
        orderDto.setTotalPrice(order.getTotalPrice());

        // Map OrderItems to OrderItemDto
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


    public CartDto getCartByCustomerId(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = customerOpt.get();
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for customer.");
        }

        return cartConventer.convertCartToDto(cart);
    }


    // Get order history for a customer (returns List<OrderDto>)
    public List<OrderDto> getOrderHistory(Long customerId) {
        // Fetch orders for the customer
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        // Map orders to OrderDto
        return orders.stream().map(order -> {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setOrderDate(order.getOrderDate());  // Order date mapping
            orderDto.setTotalPrice(order.getTotalPrice());  // Total price mapping
            orderDto.setStatus(order.getOrderStatus());  // Order status mapping
            // Optionally, map other fields like items, etc.
            return orderDto;
        }).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Map Order entity to OrderDto
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCustomerId(order.getCustomer().getId());  // Assuming Customer has getId() method
        orderDto.setOrderStatus(order.getOrderStatus());  // Ensure 'orderStatus' is present in both Order and OrderDto
        orderDto.setTotalPrice(order.getTotalPrice());  // Mapping total price
        orderDto.setOrderDate(order.getOrderDate());  // Mapping orderDate

        // Map OrderItems to OrderItemDto
        Set<OrderItemDto> orderItemDtos = new HashSet<>();
        for (OrderItem orderItem : order.getItems()) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setProductId(orderItem.getProduct().getId());  // Assuming OrderItem has Product with getId()
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setPriceAtTimeOfPurchase(orderItem.getPriceAtTimeOfPurchase());

            orderItemDtos.add(orderItemDto);
        }

        orderDto.setItems(orderItemDtos);

        return orderDto;
    }

}
