package com.dolka36.repository;

import com.dolka36.model.Product;

import java.util.*;

public class InMemoryProductRepository implements  ProductRepository{
    private final Map<String, Product> storage = new HashMap<>();


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
        List<Product> result = new ArrayList<>();
        for (Product product : storage.values()) {
            if (product.getCategory().equals(category)){
                result.add(product);
            }
        }
        return result;
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
