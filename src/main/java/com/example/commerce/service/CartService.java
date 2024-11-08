package com.example.commerce.service;

import com.example.commerce.model.*;
import com.example.commerce.repository.CartRepository;
import com.example.commerce.repository.CustomerRepository;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Cart addProductToCart(Long customerId, Long productId, int quantity) {
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
        return cart;
    }

    public Cart deleteProductFromCart(Long customerId, Long productId) {
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
        return cart;
    }

    public Cart updateCart(Long customerId, Long productId, int quantity) {
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
        return cart;
    }

    public Cart emptyCart(Long customerId) {
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
        return cart;
    }

    @Transactional
    public Order placeOrder(Long customerId) {
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


        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return order;
    }


    public Cart getCartByCustomerId(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = customerOpt.get();
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for customer.");
        }

        return cart;
    }


    public List<Order> getOrderHistory(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = customerOpt.get();

        List<Order> orders = orderRepository.findByCustomerId(customerId);

        if (orders.isEmpty()) {
            throw new IllegalArgumentException("No orders found for customer with ID: " + customerId);
        }

        return orders;
    }


    public Order getOrderById(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found for ID: " + orderId);
        }
        return orderOpt.get();
    }

}
