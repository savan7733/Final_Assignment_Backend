package org.savan.productservice.service;

import org.savan.productservice.model.Product;
import org.savan.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateStock(Long productId, Integer newStock) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setStock(newStock);
            productRepository.save(product);
        }
    }
    @KafkaListener(topics = "${kafka.topic.order-updates}", groupId = "product-service-group")
    public void listenOrderUpdates(String message) {
        System.out.println("Received order update message: " + message);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setStock(updatedProduct.getStock());
                    product.setImageId(updatedProduct.getImageId());
                    return productRepository.save(product);
                })
                .orElse(null);
    }
}
