package com.dolka36.exception;

public class OrderNotFoundException extends WarehouseException {
    public OrderNotFoundException(String orderId) {
        super("Заказ с id " + orderId + " не найден");
    }
}