package com.dolka36.repository;

import com.dolka36.model.Product;
import com.dolka36.model.Robot;
import com.dolka36.model.RobotStatus;

import java.util.*;

public class InMemoryRobotRepository implements RobotRepository{
    private final Map<String, Robot> storage = new HashMap<>();

    @Override
    public void add(Robot robot) {
        storage.put(robot.getId(), robot);
    }

    @Override
    public Optional<Robot> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Robot> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Robot> findByStatus(RobotStatus status) {
        List<Robot> result = new ArrayList<>();
        for (Robot robot : storage.values()) {
            if (robot.getStatus() == status){
                result.add(robot);
            }
        }
        return result;
    }

    @Override
    public Optional<Robot> findAvailable() {
        for (Robot robot : storage.values()) {
            if (robot.getStatus() == RobotStatus.AVAILABLE) {
                return Optional.of(robot);
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateStatus(String id, RobotStatus status) {
        Robot robot = storage.get(id);
        if(robot != null){
            robot.setStatus(status);
        }
    }

    @Override
    public void assignOrder(String robotId, String orderId) {
        Robot robot = storage.get(robotId);
        if (robot != null){
            robot.setStatus(RobotStatus.BUSY);
            robot.setCurrentOrderId(orderId);
        }
    }

    @Override
    public void releaseRobot(String id) {
        Robot robot = storage.get(id);
        if (robot != null){
            robot.setStatus(RobotStatus.AVAILABLE);
            robot.setCurrentOrderId(null);
        }
    }
}
