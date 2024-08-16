package org.savan.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Product")  // Ensure this matches your table name in MySQL
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageId;
}
