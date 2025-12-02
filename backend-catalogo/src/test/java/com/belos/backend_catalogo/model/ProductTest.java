package com.belos.backend_catalogo.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void testProductNoArgsConstructor() {
        // When
        Product product = new Product();

        // Then
        assertThat(product).isNotNull();
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isNull();
        assertThat(product.getDescription()).isNull();
        assertThat(product.getPrice()).isNull();
        assertThat(product.getQuantity()).isNull();
    }

    @Test
    void testProductAllArgsConstructor() {
        // When
        Product product = new Product(1L, "Laptop", "High-end laptop", 1200.0, 10);

        // Then
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Laptop");
        assertThat(product.getDescription()).isEqualTo("High-end laptop");
        assertThat(product.getPrice()).isEqualTo(1200.0);
        assertThat(product.getQuantity()).isEqualTo(10);
    }

    @Test
    void testProductSettersAndGetters() {
        // Given
        Product product = new Product();

        // When
        product.setId(2L);
        product.setName("Mouse");
        product.setDescription("Wireless mouse");
        product.setPrice(25.0);
        product.setQuantity(50);

        // Then
        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("Mouse");
        assertThat(product.getDescription()).isEqualTo("Wireless mouse");
        assertThat(product.getPrice()).isEqualTo(25.0);
        assertThat(product.getQuantity()).isEqualTo(50);
    }

    @Test
    void testProductWithNullId() {
        // When
        Product product = new Product(null, "Keyboard", "Mechanical keyboard", 99.99, 20);

        // Then
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isEqualTo("Keyboard");
        assertThat(product.getDescription()).isEqualTo("Mechanical keyboard");
        assertThat(product.getPrice()).isEqualTo(99.99);
        assertThat(product.getQuantity()).isEqualTo(20);
    }

    @Test
    void testProductWithZeroQuantity() {
        // When
        Product product = new Product(3L, "Out of Stock", "No items available", 50.0, 0);

        // Then
        assertThat(product.getQuantity()).isEqualTo(0);
    }

    @Test
    void testProductWithDecimalPrice() {
        // When
        Product product = new Product(4L, "Monitor", "4K Display", 349.99, 5);

        // Then
        assertThat(product.getPrice()).isEqualTo(349.99);
    }

    @Test
    void testProductUpdatePrice() {
        // Given
        Product product = new Product(5L, "Tablet", "10-inch tablet", 399.0, 8);

        // When
        product.setPrice(349.0);

        // Then
        assertThat(product.getPrice()).isEqualTo(349.0);
    }

    @Test
    void testProductUpdateQuantity() {
        // Given
        Product product = new Product(6L, "Smartphone", "Latest model", 799.0, 15);

        // When
        product.setQuantity(20);

        // Then
        assertThat(product.getQuantity()).isEqualTo(20);
    }

    @Test
    void testProductWithLongDescription() {
        // Given
        String longDescription = "This is a very long description that contains multiple words and provides detailed information about the product including specifications, features, and benefits.";

        // When
        Product product = new Product(7L, "Laptop Pro", longDescription, 1499.99, 5);

        // Then
        assertThat(product.getDescription()).isEqualTo(longDescription);
        assertThat(product.getDescription().length()).isGreaterThan(100);
    }

    @Test
    void testProductWithSpecialCharactersInName() {
        // When
        Product product = new Product(8L, "HP Laptop + Mouse & Keyboard", "Bundle pack", 999.99, 3);

        // Then
        assertThat(product.getName()).isEqualTo("HP Laptop + Mouse & Keyboard");
    }
}
