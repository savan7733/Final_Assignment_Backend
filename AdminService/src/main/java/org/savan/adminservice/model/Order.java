package org.savan.adminservice.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private Long id;
    private List<OrderItem> orderItems;
    private Double totalPrice;
}
