package org.savan.adminservice.model;

import lombok.Data;

@Data
public class OrderItem {
    private Long productId;
    private Integer quantity;
}
