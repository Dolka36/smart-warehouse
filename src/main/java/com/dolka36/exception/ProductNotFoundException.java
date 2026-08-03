package com.dolka36.exception;

public class ProductNotFoundException extends WarehouseException {
    public ProductNotFoundException(String productId) {
        super("Товар с id " + productId + " не найден");
    }
}