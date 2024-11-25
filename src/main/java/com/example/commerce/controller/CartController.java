package com.example.commerce.controller;

import com.example.commerce.dto.CartDto;
import com.example.commerce.dto.CartItemDto;
import com.example.commerce.dto.OrderDto;
import com.example.commerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;  // Constructor Injection

    // Constructor Injection
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add/{customerId}/{productId}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long customerId,
                                                    @PathVariable Long productId,
                                                    @RequestParam int quantity) {
        CartDto updatedCart = cartService.addProductToCart(customerId, productId, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @DeleteMapping("/remove/{customerId}/{productId}")
    public ResponseEntity<CartDto> removeProductFromCart(@PathVariable Long customerId,
                                                         @PathVariable Long productId) {
        CartDto updatedCart = cartService.deleteProductFromCart(customerId, productId);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PutMapping("/update/{customerId}/{productId}")
    public ResponseEntity<CartDto> updateCart(@PathVariable Long customerId,
                                              @PathVariable Long productId,
                                              @RequestParam int quantity) {
        CartDto updatedCart = cartService.updateCart(customerId, productId, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @DeleteMapping("/empty/{customerId}")
    public ResponseEntity<CartDto> emptyCart(@PathVariable Long customerId) {
        CartDto updatedCart = cartService.emptyCart(customerId);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PostMapping("/placeOrder/{customerId}")
    public ResponseEntity<OrderDto> placeOrder(@PathVariable Long customerId) {
        try {
            // Call the service and expect an OrderDto
            OrderDto order = cartService.placeOrder(customerId);
            return new ResponseEntity<>(order, HttpStatus.CREATED); // HTTP 201 Created
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // HTTP 400 Bad Request
        }
    }

    @GetMapping("/get/{customerId}")
    public ResponseEntity<CartDto> getCart(@PathVariable Long customerId) {
        try {
            CartDto cart = cartService.getCartByCustomerId(customerId);
            return new ResponseEntity<>(cart, HttpStatus.OK);  // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  // HTTP 404 Not Found
        }
    }

    @GetMapping("/order-history/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable Long customerId) {
        try {
            List<OrderDto> orders = cartService.getOrderHistory(customerId);
            return new ResponseEntity<>(orders, HttpStatus.OK); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // HTTP 404 Not Found if no orders are found
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = cartService.getOrderById(orderId);
            return new ResponseEntity<>(order, HttpStatus.OK); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // HTTP 404 Not Found if order not found
        }
    }
}

