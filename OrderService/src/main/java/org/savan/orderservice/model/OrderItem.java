package org.savan.orderservice.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class OrderItem {
    private Long productId;
    private Integer quantity;
}
