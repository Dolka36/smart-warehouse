package com.dolka36.repository;

import com.dolka36.model.Product;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements  ProductRepository{
    private final Map<String, Product> storage = new ConcurrentHashMap<>();


    @Override
    public void add(Product product) {
        storage.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return storage.values().stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
    }


    @Override
    public void updateQuantity(String id, int newQuantity) {
        Product product = storage.get(id);
        if(product != null){
            product.setQuantity(newQuantity);
        }
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }

    @Override
    public boolean exists(String id) {
       return storage.containsKey(id);
    }
}
