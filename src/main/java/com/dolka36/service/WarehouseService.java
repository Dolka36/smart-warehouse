package com.dolka36.service;

import com.dolka36.model.Order;
import com.dolka36.model.OrderStatus;
import com.dolka36.model.Product;
import com.dolka36.model.Robot;
import com.dolka36.repository.OrderRepository;
import com.dolka36.repository.ProductRepository;
import com.dolka36.repository.RobotRepository;

import java.util.List;
import java.util.Optional;

public class WarehouseService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RobotRepository robotRepository;

    public WarehouseService(ProductRepository productRepository, OrderRepository orderRepository, RobotRepository robotRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.robotRepository = robotRepository;
    }

    void addProduct(Product product){
        if (productRepository.exists(product.getId())){
            System.out.println("Товар с id " + product.getId() + " уже существует");
        } else {
            productRepository.add(product);
            System.out.println("Товар " + product.getName() + " добавлен на склад");
        }
    }

    List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    void createOrder(String orderId, String productId, int quantity){
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            System.out.println("Товар не найден");
            return;
        }
        Product product = productOpt.get();

        if (product.getQuantity() < quantity) {
            System.out.println("Недостаточно товара на складе. Доступно: " + product.getQuantity());
            return;
        }

        Order order = new Order(orderId, productId, quantity);
        orderRepository.add(order);
        System.out.println("Заказ " + orderId + " создан на товар " + product.getName());
    }

    void processNextOrder(){
        Optional<Robot> robotOpt = robotRepository.findAvailable();
        if (robotOpt.isEmpty()) {
            System.out.println("Нет свободных роботов");
            return;
        }
        Robot robot = robotOpt.get();
        List<Order> newOrders = orderRepository.findByStatus(OrderStatus.NEW);

        if (newOrders.isEmpty()) {
            System.out.println("Нет новых заказов");
            return;
        }

        Order order = newOrders.get(0);

        robotRepository.assignOrder(robot.getId(), order.getId());
        orderRepository.updateStatus(order.getId(), OrderStatus.PROCESSING);
        System.out.println("Робот " + robot.getName() + " взял в работу заказ " + order.getId());
    }

    void completeOrder(String orderId){
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            System.out.println("Заказ не найден");
            return;
        }
        Order order = orderOpt.get();

        if (order.getStatus() != OrderStatus.PROCESSING) {
            System.out.println("Заказ не находится в обработке");
            return;
        }

        Optional<Product> productOpt = productRepository.findById(order.getProductId());

        Product product = productOpt.get();

        int newQuantity = product.getQuantity() - order.getQuantity();

        productRepository.updateQuantity(product.getId(), newQuantity);
        orderRepository.updateStatus(orderId, OrderStatus.COMPLETED);

        List<Robot> allRobots = robotRepository.findAll();

        for (Robot robot : allRobots) {
            if (orderId.equals(robot.getCurrentOrderId())) {
                robotRepository.releaseRobot(robot.getId());
                break;
            }
        }
        System.out.println("Заказ " + orderId + " выполнен. Товар " + product.getName() + ", остаток: " + newQuantity);
    }
}
