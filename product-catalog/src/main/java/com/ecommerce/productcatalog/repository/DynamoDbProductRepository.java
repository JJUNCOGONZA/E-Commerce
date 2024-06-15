package com.ecommerce.productcatalog.repository;

import com.ecommerce.productcatalog.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DynamoDbProductRepository {

    @Autowired
    private DynamoDbClient dynamoDbClient;

    private final String TABLE_NAME = "Products";

    public void save(Product product) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Id", AttributeValue.builder().s(product.getId()).build());
        item.put("Name", AttributeValue.builder().s(product.getName()).build());
        item.put("Category", AttributeValue.builder().s(product.getCategory()).build());
        item.put("Price", AttributeValue.builder().n(String.valueOf(product.getPrice())).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public Product findById(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Id", AttributeValue.builder().s(id).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
        if (returnedItem != null && !returnedItem.isEmpty()) {
            String name = returnedItem.get("Name").s();
            String category = returnedItem.get("Category").s();
            double price = Double.parseDouble(returnedItem.get("Price").n());
            return new Product(id, name, category, price);
        } else {
            return null;
        }
    }

    public Product findByName(String name) {
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#name", "Name");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":name", AttributeValue.builder().s(name).build());

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .filterExpression("#name = :name")
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        List<Map<String, AttributeValue>> items = scanResponse.items();

        if (items != null && !items.isEmpty()) {
            Map<String, AttributeValue> item = items.get(0);
            String id = item.get("Id").s();
            String category = item.get("Category").s();
            double price = Double.parseDouble(item.get("Price").n());
            return new Product(id, name, category, price);
        } else {
            return null;
        }
    }

    public List<Product> findAll() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        List<Map<String, AttributeValue>> items = scanResponse.items();
        List<Product> products = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            String id = item.get("Id").s();
            String name = item.get("Name").s();
            String category = item.get("Category").s();
            double price = Double.parseDouble(item.get("Price").n());
            products.add(new Product(id, name, category, price));
        }
        return products;
    }

    public void update(String id, Product product) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Id", AttributeValue.builder().s(product.getId()).build());
        item.put("Name", AttributeValue.builder().s(product.getName()).build());
        item.put("Category", AttributeValue.builder().s(product.getCategory()).build());
        item.put("Price", AttributeValue.builder().n(String.valueOf(product.getPrice())).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public void delete(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Id", AttributeValue.builder().s(id).build());

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        dynamoDbClient.deleteItem(request);
    }
}
