package com.example.commerce.converters;
import com.example.commerce.dto.OrderDto;
import com.example.commerce.dto.OrderItemDto;
import com.example.commerce.model.Order;
import com.example.commerce.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OrderConventer {

    // Convert Order entity to OrderDto
    public OrderDto convertOrderToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setStatus(order.getOrderStatus());

        Set<OrderItemDto> orderItemDtos = new HashSet<>();
        for (OrderItem orderItem : order.getItems()) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setProductId(orderItem.getProduct().getId());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setPriceAtTimeOfPurchase(orderItem.getPriceAtTimeOfPurchase());
            orderItemDtos.add(orderItemDto);
        }
        orderDto.setItems(orderItemDtos);

        return orderDto;
    }

    // Convert OrderDto to Order entity
    public Order convertDtoToOrder(OrderDto orderDto) {
        // Implement the reverse logic here to convert OrderDto to Order entity
        Order order = new Order();
        // Your logic here
        return order;
    }

}
