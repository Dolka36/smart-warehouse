package com.dolka36.exception;

public class NoAvailableRobotsException extends WarehouseException {
    public NoAvailableRobotsException() {
        super("Нет свободных роботов");
    }
}