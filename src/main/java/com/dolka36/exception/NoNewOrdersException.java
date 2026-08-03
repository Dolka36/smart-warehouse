package com.dolka36.exception;

public class NoNewOrdersException extends WarehouseException {
    public NoNewOrdersException() {
        super("Нет новых заказов");
    }
}