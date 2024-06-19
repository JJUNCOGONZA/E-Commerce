package com.ecommerce.productcatalog.controller;

import com.ecommerce.productcatalog.domain.Product;
import com.ecommerce.productcatalog.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller", description = "API for product catalog management")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Adds a new product to the catalog.
     * @param product the product to be added
     * @return a response entity with a success message and HTTP status
     */
    @PostMapping
    @Operation(summary = "Add a new product")
    public ResponseEntity<String> addProduct(@Valid @RequestBody Product product) {
        try {
            productService.addProduct(product);
            return new ResponseEntity<>("Product created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Retrieves a product by its ID.
     * @param id the ID of the product to be retrieved
     * @return a response entity with the product and HTTP status
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all products in the catalog.
     * @return a response entity with a list of all products and HTTP status
     */
    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Updates an existing product by its ID.
     * @param id the ID of the product to be updated
     * @param product the updated product details
     * @return a response entity with a success message and HTTP status
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a product by ID")
    public ResponseEntity<String> updateProduct(@PathVariable String id, @Valid @RequestBody Product product) {
        if (productService.getProductById(id) != null) {
            productService.updateProduct(id, product);
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a product by its ID.
     * @param id the ID of the product to be deleted
     * @return a response entity with a success message and HTTP status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        if (productService.getProductById(id) != null) {
            productService.deleteProduct(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
