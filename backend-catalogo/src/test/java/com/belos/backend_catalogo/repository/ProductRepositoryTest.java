package com.belos.backend_catalogo.repository;

import com.belos.backend_catalogo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testSaveProduct() {
        // Given
        Product product = new Product(null, "Laptop", "High-end laptop", 1200.0, 10);

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Laptop");
        assertThat(savedProduct.getDescription()).isEqualTo("High-end laptop");
        assertThat(savedProduct.getPrice()).isEqualTo(1200.0);
        assertThat(savedProduct.getQuantity()).isEqualTo(10);
    }

    @Test
    void testFindAll_Empty() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void testFindAll_WithProducts() {
        // Given
        Product product1 = new Product(null, "Mouse", "Wireless mouse", 25.0, 50);
        Product product2 = new Product(null, "Keyboard", "Mechanical keyboard", 99.99, 20);
        productRepository.save(product1);
        productRepository.save(product2);

        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("Mouse", "Keyboard");
    }

    @Test
    void testFindById_Found() {
        // Given
        Product product = new Product(null, "Monitor", "4K Monitor", 349.0, 5);
        Product savedProduct = productRepository.save(product);

        // When
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Monitor");
        assertThat(foundProduct.get().getDescription()).isEqualTo("4K Monitor");
    }

    @Test
    void testFindById_NotFound() {
        // When
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Then
        assertThat(foundProduct).isEmpty();
    }

    @Test
    void testDeleteProduct() {
        // Given
        Product product = new Product(null, "Smartphone", "Latest model", 799.0, 15);
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        // When
        productRepository.deleteById(productId);

        // Then
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    void testUpdateProduct() {
        // Given
        Product product = new Product(null, "Tablet", "10-inch tablet", 399.0, 8);
        Product savedProduct = productRepository.save(product);

        // When
        savedProduct.setPrice(349.0);
        savedProduct.setQuantity(12);
        Product updatedProduct = productRepository.save(savedProduct);

        // Then
        assertThat(updatedProduct.getPrice()).isEqualTo(349.0);
        assertThat(updatedProduct.getQuantity()).isEqualTo(12);
        assertThat(updatedProduct.getName()).isEqualTo("Tablet");
    }

    @Test
    void testSaveAll() {
        // Given
        Product product1 = new Product(null, "Mouse", "Gaming mouse", 79.99, 30);
        Product product2 = new Product(null, "Headset", "Wireless headset", 149.99, 15);
        Product product3 = new Product(null, "Webcam", "HD webcam", 89.99, 20);

        // When
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2, product3));

        // Then
        assertThat(savedProducts).hasSize(3);
        assertThat(productRepository.findAll()).hasSize(3);
    }

    @Test
    void testCount() {
        // Given
        Product product1 = new Product(null, "Mouse", "Gaming mouse", 79.99, 30);
        Product product2 = new Product(null, "Keyboard", "Mechanical keyboard", 99.99, 20);
        productRepository.save(product1);
        productRepository.save(product2);

        // When
        long count = productRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistsById() {
        // Given
        Product product = new Product(null, "Monitor", "27-inch monitor", 299.0, 10);
        Product savedProduct = productRepository.save(product);

        // When & Then
        assertThat(productRepository.existsById(savedProduct.getId())).isTrue();
        assertThat(productRepository.existsById(999L)).isFalse();
    }
}
