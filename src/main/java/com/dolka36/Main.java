package com.dolka36;

import com.dolka36.exception.WarehouseException;
import com.dolka36.model.Order;
import com.dolka36.model.Product;
import com.dolka36.model.Robot;
import com.dolka36.repository.InMemoryOrderRepository;
import com.dolka36.repository.InMemoryProductRepository;
import com.dolka36.repository.InMemoryRobotRepository;
import com.dolka36.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryProductRepository productRepository = new InMemoryProductRepository();
        InMemoryRobotRepository robotRepository = new InMemoryRobotRepository();

        WarehouseService warehouseService = new WarehouseService(productRepository, orderRepository, robotRepository);

        // Добавляем тестовых роботов при старте
        robotRepository.add(new Robot("ROB-001", "Робот-погрузчик Альфа"));
        robotRepository.add(new Robot("ROB-002", "Робот-погрузчик Бета"));
        log.info("Система склада запущена. Роботы готовы к работе.");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== УМНЫЙ СКЛАД =====");
            System.out.println("1. Показать все товары");
            System.out.println("2. Добавить товар");
            System.out.println("3. Создать заказ");
            System.out.println("4. Обработать следующий заказ");
            System.out.println("5. Завершить заказ");
            System.out.println("6. Показать все заказы");
            System.out.println("7. Показать всех роботов");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    List<Product> products = warehouseService.getAllProducts();
                    if (products.isEmpty()) {
                        log.info("Склад пуст");
                    } else {
                        System.out.println("Товары на складе:");
                        for (Product p : products) {
                            System.out.println("  " + p);
                        }
                    }
                    break;
                case 2:
                    System.out.print("Введите id товара: ");
                    String id = scanner.nextLine();
                    System.out.print("Введите название: ");
                    String name = scanner.nextLine();
                    System.out.print("Введите категорию: ");
                    String category = scanner.nextLine();
                    System.out.print("Введите цену: ");
                    double price = scanner.nextDouble();
                    System.out.print("Введите количество: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // поглотить Enter

                    Product newProduct = new Product(id, name, category, price, quantity);
                    try {
                        warehouseService.addProduct(newProduct);
                    } catch (WarehouseException e) {
                        log.error(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Введите id заказа: ");
                    String orderId = scanner.nextLine();
                    System.out.print("Введите id товара: ");
                    String productId = scanner.nextLine();
                    System.out.print("Введите количество: ");
                    int qty = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        warehouseService.createOrder(orderId, productId, qty);
                    } catch (WarehouseException e) {
                        log.error(e.getMessage());
                    }
                    break;
                case 4:
                    try {
                        warehouseService.processNextOrder();
                    } catch (WarehouseException e) {
                        log.error(e.getMessage());
                    }
                    break;
                case 5:
                    System.out.print("Введите id заказа для завершения: ");
                    String completeOrderId = scanner.nextLine();
                    try {
                        warehouseService.completeOrder(completeOrderId);
                    } catch (WarehouseException e) {
                        log.error(e.getMessage());
                    } catch (IllegalStateException e) {
                        log.error(e.getMessage());
                    }
                    break;
                case 6:
                    List<Order> orders = warehouseService.getAllOrders();
                    if (orders.isEmpty()) {
                        log.info("Заказов нет");
                    } else {
                        System.out.println("Все заказы:");
                        for (Order o : orders) {
                            System.out.println("  " + o);
                        }
                    }
                    break;
                case 7:
                    List<Robot> robots = warehouseService.getAllRobots();
                    if (robots.isEmpty()) {
                        log.info("Роботов нет");
                    } else {
                        System.out.println("Все роботы:");
                        for (Robot r : robots) {
                            System.out.println("  " + r);
                        }
                    }
                    break;
                case 0:
                    log.info("Выход из программы. До свидания!");
                    scanner.close();
                    return;
                default:
                    log.warn("Неверный выбор: {}", choice);
            }
        }
    }
}