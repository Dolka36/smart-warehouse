package com.dolka36.repository;

import com.dolka36.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void add(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    void updateQuantity(String id, int newQuantity);
    void delete(String id);
    boolean exists(String id);
}
