package org.savan.orderservice.service;

import org.savan.orderservice.model.Order;
import org.savan.orderservice.model.OrderItem;
import org.savan.orderservice.model.Product;
import org.savan.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private RestTemplate restTemplate; // Inject RestTemplate

//    @Autowired
//    private ProductService productService;



    @Value("${kafka.topic.order-updates}")
    private String orderUpdatesTopic;

    @Value("${product.service.url}")
    private String productServiceUrl; // URL of ProductService

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order createOrder(Order order) {
        // Calculate total price
        double totalPrice = calculateTotalPrice(order.getOrderItems());
        order.setTotalPrice(totalPrice);

        // Check stock availability
        if (!checkStockAvailability(order.getOrderItems())) {
            throw new RuntimeException("Insufficient stock for some products.");
        }

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Produce Kafka message
        kafkaTemplate.send(orderUpdatesTopic, "Order Created: " + savedOrder.getId());

        // Update product stock
        updateProductStock(order.getOrderItems());

        return savedOrder;
    }

    private double calculateTotalPrice(List<OrderItem> orderItems) {
        double totalPrice = 0.0;
        for (OrderItem item : orderItems) {
            Product product = restTemplate.getForObject(productServiceUrl + item.getProductId(), Product.class);
            if (product != null) {
                totalPrice += product.getPrice() * item.getQuantity();
            }
        }
        return totalPrice;
    }

    private boolean checkStockAvailability(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = restTemplate.getForObject(productServiceUrl +  item.getProductId(), Product.class);
            if (product == null || product.getStock() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    private Product getProductById(Long productId) {
        String url = productServiceUrl + "/" + productId;
        return restTemplate.getForObject(url, Product.class);
    }

    private void updateProductStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = getProductById(item.getProductId());
            if (product != null) {
                int updatedStock = product.getStock() - item.getQuantity();
                product.setStock(updatedStock);
                // Assuming there's an API to update stock in ProductService
                String updateUrl = productServiceUrl + "/" + product.getId() + "/" + product.getStock();
                restTemplate.put(updateUrl, product);
            }
        }

    }
}
