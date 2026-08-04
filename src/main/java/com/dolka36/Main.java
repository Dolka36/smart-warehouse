package com.dolka36;

import com.dolka36.exception.WarehouseException;
import com.dolka36.model.Order;
import com.dolka36.model.Product;
import com.dolka36.model.Robot;
import com.dolka36.repository.InMemoryOrderRepository;
import com.dolka36.repository.InMemoryProductRepository;
import com.dolka36.repository.InMemoryRobotRepository;
import com.dolka36.service.RobotWorker;
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
    private static final List<Thread> robotThreads = new ArrayList<>();
    private static final List<RobotWorker> robotWorkers = new ArrayList<>();

    public static void main(String[] args) {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryProductRepository productRepository = new InMemoryProductRepository();
        InMemoryRobotRepository robotRepository = new InMemoryRobotRepository();

        WarehouseService warehouseService = new WarehouseService(productRepository, orderRepository, robotRepository);

        robotRepository.add(new Robot("ROB-001", "Робот-погрузчик Альфа"));
        robotRepository.add(new Robot("ROB-002", "Робот-погрузчик Бета"));
        log.info("Система склада запущена. Роботы готовы к работе.");

        List<Robot> allRobots = warehouseService.getAllRobots();
        for (Robot robot : allRobots) {
            RobotWorker worker = new RobotWorker(robot, warehouseService);
            robotWorkers.add(worker);
        }
        log.info("Создано {} роботов-исполнителей", robotWorkers.size());

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
            System.out.println("8. Запустить многопоточную обработку");
            System.out.println("9. Остановить всех роботов");
            System.out.println("10. Общая стоимость товаров");
            System.out.println("11. Товары с низким остатком");
            System.out.println("12. Товары по категории (сорт. по цене)");
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
                    scanner.nextLine();

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
                case 8:
                    robotThreads.clear();
                    for (RobotWorker worker : robotWorkers) {
                        Thread thread = new Thread(worker, worker.getRobotName()); // нужен геттер!
                        robotThreads.add(thread);
                        thread.start();
                    }
                    log.info("Запущено {} потоков-роботов", robotThreads.size());
                    break;

                case 9:
                    for (RobotWorker worker : robotWorkers) {
                        worker.stop();
                    }
                    for (Thread thread : robotThreads) {
                        try {
                            thread.join(5000); // ждём завершения потока до 5 секунд
                        } catch (InterruptedException e) {
                            log.warn("Прервано ожидание завершения потока");
                        }
                    }
                    robotThreads.clear();
                    log.info("Все роботы остановлены");
                    break;
                case 10:
                    double totalValue = warehouseService.getTotalStockValue();
                    System.out.printf("Общая стоимость товаров на складе: %.2f%n", totalValue);
                    break;

                case 11:
                    System.out.print("Введите порог остатка: ");
                    int threshold = scanner.nextInt();
                    scanner.nextLine();
                    List<Product> lowStock = warehouseService.getLowStockProducts(threshold);
                    if (lowStock.isEmpty()) {
                        log.info("Товаров с остатком ниже {} не найдено", threshold);
                    } else {
                        System.out.println("Товары с низким остатком:");
                        lowStock.forEach(p -> System.out.println("  " + p));
                    }
                    break;

                case 12:
                    System.out.print("Введите категорию: ");
                    String cat = scanner.nextLine();
                    List<Product> byCategory = warehouseService.getProductsByCategorySortedByPrice(cat);
                    if (byCategory.isEmpty()) {
                        log.info("Товары категории '{}' не найдены", cat);
                    } else {
                        System.out.println("Товары категории '" + cat + "' (сорт. по цене):");
                        byCategory.forEach(p -> System.out.println("  " + p));
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