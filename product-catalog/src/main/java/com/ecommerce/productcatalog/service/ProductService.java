package com.ecommerce.productcatalog.service;

import com.ecommerce.productcatalog.domain.Product;
import com.ecommerce.productcatalog.repository.DynamoDbProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private DynamoDbProductRepository productRepository;

    public void addProduct(Product product) {
        if (productRepository.findByName(product.getName()) != null) {
            throw new IllegalArgumentException("Product with the same name already exists");
        }
        product.setId(UUID.randomUUID().toString());
        productRepository.save(product);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void updateProduct(String id, Product product) {
        product.setId(id);
        productRepository.update(id, product);
    }

    public void deleteProduct(String id) {
        productRepository.delete(id);
    }
}
