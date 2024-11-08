package com.example.commerce.controller;

import com.example.commerce.model.Cart;
import com.example.commerce.model.Order;
import com.example.commerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{customerId}/{productId}")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long customerId,
                                                 @PathVariable Long productId,
                                                 @RequestParam int quantity) {
        Cart updatedCart = cartService.addProductToCart(customerId, productId, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @DeleteMapping("/remove/{customerId}/{productId}")
    public ResponseEntity<Cart> removeProductFromCart(@PathVariable Long customerId,
                                                      @PathVariable Long productId) {
        Cart updatedCart = cartService.deleteProductFromCart(customerId, productId);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PutMapping("/update/{customerId}/{productId}")
    public ResponseEntity<Cart> updateCart(@PathVariable Long customerId,
                                           @PathVariable Long productId,
                                           @RequestParam int quantity) {
        Cart updatedCart = cartService.updateCart(customerId, productId, quantity);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @DeleteMapping("/empty/{customerId}")
    public ResponseEntity<Cart> emptyCart(@PathVariable Long customerId) {
        Cart updatedCart = cartService.emptyCart(customerId);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }



    @PostMapping("/placeOrder/{customerId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long customerId) {
        try {
            Order order = cartService.placeOrder(customerId);
            return new ResponseEntity<>(order, HttpStatus.CREATED); // HTTP 201 Created
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // HTTP 400 Bad Request
        }
    }

    @GetMapping("/get/{customerId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long customerId) {
        try {
            Cart cart = cartService.getCartByCustomerId(customerId);
            return new ResponseEntity<>(cart, HttpStatus.OK);  // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  // HTTP 404 Not Found
        }
    }

    @GetMapping("/order-history/{customerId}")
    public ResponseEntity<List<Order>> getOrderHistory(@PathVariable Long customerId) {
        try {
            List<Order> orders = cartService.getOrderHistory(customerId);
            return new ResponseEntity<>(orders, HttpStatus.OK); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // HTTP 404 Not Found if no orders are found
        }
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = cartService.getOrderById(orderId);
            return new ResponseEntity<>(order, HttpStatus.OK); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // HTTP 404 Not Found if order not found
        }
    }

}
