package com.belos.backend_catalogo.controller;

import com.belos.backend_catalogo.model.Product;
import com.belos.backend_catalogo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    }

    @PostMapping("/api/products")
    public Product createProduct(@RequestBody Product product) {
        var newProduct = productRepository.save(product);
        return newProduct;
    }
}
