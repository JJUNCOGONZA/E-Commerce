package com.ecommerce.order.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.domain.Order;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DynamoDbOrderRepository {

    @Autowired
    private DynamoDbClient dynamoDbClient;

    private final String TABLE_NAME = "Orders";

    public void save(Order order) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("OrderId", AttributeValue.builder().s(order.getId()).build());
        item.put("ProductId", AttributeValue.builder().s(order.getProductId()).build());
        item.put("Quantity", AttributeValue.builder().n(String.valueOf(order.getQuantity())).build());
        item.put("Creation", AttributeValue.builder().s(currentDateTime).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
}

