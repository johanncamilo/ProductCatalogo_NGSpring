package com.belos.backend_catalogo.controller;

import com.belos.backend_catalogo.model.Product;
import com.belos.backend_catalogo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testIntroMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome to this application"))
                .andExpect(jsonPath("$.age").value(21));
    }

    @Test
    void testGetAllProducts_EmptyList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllProducts_WithProducts() throws Exception {
        // Given
        Product product1 = new Product(null, "Laptop", "High-end laptop", 1200.0, 10);
        Product product2 = new Product(null, "Mouse", "Wireless mouse", 25.0, 50);
        
        productRepository.saveAll(List.of(product1, product2));

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].description").value("High-end laptop"))
                .andExpect(jsonPath("$[0].price").value(1200.0))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[1].name").value("Mouse"))
                .andExpect(jsonPath("$[1].description").value("Wireless mouse"))
                .andExpect(jsonPath("$[1].price").value(25.0))
                .andExpect(jsonPath("$[1].quantity").value(50));
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        // Given
        Product product = new Product(null, "Keyboard", "Mechanical keyboard", 99.99, 20);
        String productJson = objectMapper.writeValueAsString(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.description").value("Mechanical keyboard"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void testCreateProduct_VerifyPersistence() throws Exception {
        // Given
        Product product = new Product(null, "Monitor", "4K Monitor", 349.0, 5);
        String productJson = objectMapper.writeValueAsString(product);

        // When
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk());

        // Then
        List<Product> products = productRepository.findAll();
        assert products.size() == 1;
        assert products.get(0).getName().equals("Monitor");
        assert products.get(0).getDescription().equals("4K Monitor");
        assert products.get(0).getPrice() == 349.0;
        assert products.get(0).getQuantity() == 5;
    }

    @Test
    void testCreateMultipleProducts() throws Exception {
        // Given
        Product product1 = new Product(null, "Smartphone", "Latest model", 799.0, 15);
        Product product2 = new Product(null, "Tablet", "10-inch tablet", 399.0, 8);

        // When
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testCreateProduct_WithSpecialCharacters() throws Exception {
        // Given
        Product product = new Product(null, "Laptop HP", "Core i7 + 16GB RAM", 1499.99, 3);
        String productJson = objectMapper.writeValueAsString(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop HP"))
                .andExpect(jsonPath("$.description").value("Core i7 + 16GB RAM"));
    }

    @Test
    void testCreateProduct_WithZeroQuantity() throws Exception {
        // Given
        Product product = new Product(null, "Out of Stock Item", "No stock", 50.0, 0);
        String productJson = objectMapper.writeValueAsString(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(0));
    }
}
