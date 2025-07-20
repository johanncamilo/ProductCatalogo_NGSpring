package com.belos.backend_catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.belos.backend_catalogo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}
