package com.dolka36.repository;

import com.dolka36.model.Order;
import com.dolka36.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void add(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    List<Order> findByStatus(OrderStatus status);
    void updateStatus(String id, OrderStatus newStatus);
    boolean exists(String id);
}
