package org.savan.orderservice.model;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageId;
}
