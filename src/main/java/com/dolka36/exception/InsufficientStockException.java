package com.dolka36.exception;

public class InsufficientStockException extends WarehouseException {
    public InsufficientStockException(String productId, int requested, int available) {
        super("Недостаточно товара " + productId + ". Запрошено: " + requested + ", доступно: " + available);
    }
}