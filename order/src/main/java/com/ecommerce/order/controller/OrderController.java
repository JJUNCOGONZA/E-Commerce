package com.ecommerce.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.order.domain.Order;
import com.ecommerce.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Controller", description = "API for Order management")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Adds a new order.
     * @param order the order to be added
     * @return a response entity with a success message and HTTP status
     */
    @PostMapping
    @Operation(summary = "Add a new order")
    public ResponseEntity<String> addOrder(@Valid @RequestBody Order order) {
        try {
            orderService.addOrder(order);
            return new ResponseEntity<>("Order created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
