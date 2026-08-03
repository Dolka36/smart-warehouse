package com.dolka36.exception;

public class ProductAlreadyExistsException extends WarehouseException {
    public ProductAlreadyExistsException(String productId) {
        super("Товар с id " + productId + " уже существует");
    }
}