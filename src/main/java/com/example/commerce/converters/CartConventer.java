package com.example.commerce.converters;
import com.example.commerce.dto.CartDto;
import com.example.commerce.dto.CartItemDto;
import com.example.commerce.model.Cart;
import com.example.commerce.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CartConventer {

    // Convert Cart entity to CartDto
    public CartDto convertCartToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setCustomerId(cart.getCustomer().getId());
        cartDto.setTotalPrice(cart.getTotalPrice());

        Set<CartItemDto> itemDtos = new HashSet<>();
        for (CartItem cartItem : cart.getItems()) {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setProductId(cartItem.getProduct().getId());
            itemDto.setQuantity(cartItem.getQuantity());
            itemDto.setPriceAtTimeOfAdd(cartItem.getPriceAtTimeOfAdd());
            itemDtos.add(itemDto);
        }
        cartDto.setItems(itemDtos);

        return cartDto;
    }

    // Convert CartDto to Cart entity
    public Cart convertDtoToCart(CartDto cartDto) {
        // Implement logic to convert CartDto to Cart entity
        // Assuming Cart has a set of CartItems that can be mapped from CartItemDto
        Cart cart = new Cart();
        // Your logic here
        return cart;
    }


}
