package com.dolka36.repository;

import com.dolka36.model.Robot;
import com.dolka36.model.RobotStatus;

import java.util.List;
import java.util.Optional;

public interface RobotRepository {
    void add(Robot robot);
    Optional<Robot> findById(String id);
    List<Robot> findAll();
    List<Robot> findByStatus(RobotStatus status);
    Optional<Robot> findAvailable();
    void updateStatus(String id, RobotStatus status);
    void assignOrder(String robotId, String orderId);
    void releaseRobot(String id);
}
