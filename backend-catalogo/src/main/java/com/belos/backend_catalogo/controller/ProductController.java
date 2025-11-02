package com.belos.backend_catalogo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.belos.backend_catalogo.model.Product;
import com.belos.backend_catalogo.repository.ProductRepository;

@RestController
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public Map<String, Object> introMessage() {
        return Map.of("message", "Welcome to this application", "age", 21);
    }

    @GetMapping("/api/products")
    public List<Product> getAllProducts() {

        return productRepository.findAll();

        // return List.of(
        // new Product(1L, "Laptop", "laptop muy bonito", 5000.0, 100),
        // new Product(2L, "Mouse", "mouse muy bonito", 25d, 500));
    }

    @PostMapping("/api/products")
    public Product createProduct(@RequestBody Product product) {
        var newProduct = productRepository.save(product);
        return newProduct;
    }
}
