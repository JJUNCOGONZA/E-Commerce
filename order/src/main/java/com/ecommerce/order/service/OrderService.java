package com.ecommerce.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.order.domain.Order;
import com.ecommerce.order.repository.DynamoDbOrderRepository;

import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private DynamoDbOrderRepository orderRepository;

    public void addOrder(Order order) {
        order.setId(UUID.randomUUID().toString());
        orderRepository.save(order);
    }
}
