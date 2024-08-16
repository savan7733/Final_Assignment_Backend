package org.savan.adminservice.service;
import org.savan.adminservice.model.Order;
import org.savan.adminservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public List<Product> getAllProducts() {
        Product[] products = restTemplate.getForObject(productServiceUrl, Product[].class);
        return Arrays.asList(products);
    }

    public Product createProduct(Product product) {
        return restTemplate.postForObject(productServiceUrl, product, Product.class);
    }

    public Product updateProduct(Long id, Product product) {
        restTemplate.put(productServiceUrl + "/" + id, product);
        return product;
    }

    public void deleteProduct(Long id) {
        restTemplate.delete(productServiceUrl + "/" + id);
    }

    public List<Order> getAllOrders() {
        Order[] orders = restTemplate.getForObject(orderServiceUrl, Order[].class);
        return Arrays.asList(orders);
    }
}
