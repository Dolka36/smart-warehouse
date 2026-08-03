package com.dolka36.repository;

import com.dolka36.model.Order;
import com.dolka36.model.OrderStatus;
import com.dolka36.model.Product;

import java.util.*;

public class InMemoryOrderRepository implements  OrderRepository{
    private final Map<String, Order> storage = new HashMap<>();

    @Override
    public void add(Order order) {
        storage.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        List<Order> result = new ArrayList<>();
        for (Order order : storage.values()) {
            if (order.getStatus().equals(status)){
                result.add(order);
            }
        }
        return result;
    }

    @Override
    public void updateStatus(String id, OrderStatus newStatus) {
        Order order = storage.get(id);
        if(order != null){
            order.setStatus(newStatus);
        }
    }

    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }
}
