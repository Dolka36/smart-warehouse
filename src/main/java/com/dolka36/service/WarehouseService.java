package com.dolka36.service;

import com.dolka36.exception.*;
import com.dolka36.model.Order;
import com.dolka36.model.OrderStatus;
import com.dolka36.model.Product;
import com.dolka36.model.Robot;
import com.dolka36.repository.OrderRepository;
import com.dolka36.repository.ProductRepository;
import com.dolka36.repository.RobotRepository;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

public class WarehouseService {
    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RobotRepository robotRepository;

    public WarehouseService(ProductRepository productRepository, OrderRepository orderRepository, RobotRepository robotRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.robotRepository = robotRepository;
    }

    public void addProduct(Product product) throws ProductAlreadyExistsException {
        if (productRepository.exists(product.getId())){
            throw new ProductAlreadyExistsException(product.getId());
        }
            productRepository.add(product);
            log.info("Товар {} добавлен на склад", product.getName());
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public void createOrder(String orderId, String productId, int quantity) throws ProductNotFoundException, InsufficientStockException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        Product product = productOpt.get();

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getQuantity());
        }

        Order order = new Order(orderId, productId, quantity);
        orderRepository.add(order);
        log.info("Заказ {} создан на товар {}", orderId, product.getName());
    }

    public void processNextOrder() throws NoAvailableRobotsException, NoNewOrdersException {
        Optional<Robot> robotOpt = robotRepository.findAvailable();
        if (robotOpt.isEmpty()) {
            throw new NoAvailableRobotsException();
        }
        Robot robot = robotOpt.get();
        List<Order> newOrders = orderRepository.findByStatus(OrderStatus.NEW);

        if (newOrders.isEmpty()) {
            throw new NoNewOrdersException();
        }

        Order order = newOrders.get(0);

        robotRepository.assignOrder(robot.getId(), order.getId());
        orderRepository.updateStatus(order.getId(), OrderStatus.PROCESSING);
        log.info("Робот {} взял в работу заказ {}", robot.getName(), order.getId());
    }

    public void completeOrder(String orderId) throws OrderNotFoundException, ProductNotFoundException {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        Order order = orderOpt.get();

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Заказ " + orderId + " не находится в обработке");
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
        log.info("Заказ {} выполнен. Товар {}, остаток: {}", orderId, product.getName(), newQuantity);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Robot> getAllRobots() {
        return robotRepository.findAll();
    }
}
