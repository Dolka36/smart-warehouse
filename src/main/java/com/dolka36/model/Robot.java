package com.dolka36.model;

import java.util.Objects;

public class Robot {
    private String id;
    private String name;
    private RobotStatus status;
    private String currentOrderId;

    public Robot(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = RobotStatus.AVAILABLE;
        this.currentOrderId = null;
    }

    public String getName() {
        return name;
    }

    public RobotStatus getStatus() {
        return status;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public String getId() {
        return id;
    }

    public void setStatus(RobotStatus status) {
        this.status = status;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    @Override
    public String toString() {
        return "Robot{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", currentOrderId='" + (currentOrderId != null ? currentOrderId : "нет активного заказа") + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Robot robot = (Robot) o;
        return Objects.equals(id, robot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
