package com.ecommerce.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;

    private Date creation;

    @NotBlank(message = "Product is mandatory")
    private String productId;

    @NotNull(message = "Quantity is mandatory")
    private Double quantity;
}
