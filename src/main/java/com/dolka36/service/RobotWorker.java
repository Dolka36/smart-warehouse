package com.dolka36.service;

import com.dolka36.model.Order;
import com.dolka36.model.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RobotWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RobotWorker.class);

    private final Robot robot;
    private final WarehouseService warehouseService;
    private volatile boolean running = true;

    public RobotWorker(Robot robot, WarehouseService warehouseService) {
        this.robot = robot;
        this.warehouseService = warehouseService;
    }

    @Override
    public void run() {
        log.info("{} запущен и готов к работе", robot.getName());

        while (running) {
            try {
                Optional<Order> orderOpt = warehouseService.tryTakeOrder(robot.getId());

                if (orderOpt.isPresent()) {
                    Thread.sleep(2000);

                    warehouseService.finishOrder(robot.getId());
                } else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                log.warn("{} прерван во время ожидания", robot.getName());
                Thread.currentThread().interrupt(); // восстанавливаем флаг прерывания
                break;
            }
        }

        log.info("{} остановлен", robot.getName());
    }

    public void stop() {
        running = false;
    }

    public String getRobotName() {
        return robot.getName();
    }
}