package com.enyoi.arka.adapters.out.repository.entity;

import com.enyoi.arka.domain.entities.OrderItem;
import com.enyoi.arka.domain.valueobjects.Money;
import com.enyoi.arka.domain.valueobjects.ProductId;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @Column(name = "id", length = 100)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPriceAmount;

    @Column(name = "unit_price_currency", nullable = false, length = 3)
    private String unitPriceCurrency;

    public OrderItemEntity() {
    }

    public static OrderItemEntity fromDomain(OrderItem item, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.id = java.util.UUID.randomUUID().toString();
        entity.order = order;
        entity.productId = item.getProductId().value();
        entity.quantity = item.getQuantity();
        entity.unitPriceAmount = item.getUnitPrice().amount();
        entity.unitPriceCurrency = item.getUnitPrice().currency().getCurrencyCode();
        return entity;
    }

    public OrderItem toDomain() {
        return OrderItem.builder()
                .productId(ProductId.of(productId))
                .quantity(quantity)
                .unitPrice(Money.of(unitPriceAmount, unitPriceCurrency))
                .build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public void setUnitPriceAmount(BigDecimal unitPriceAmount) {
        this.unitPriceAmount = unitPriceAmount;
    }

    public String getUnitPriceCurrency() {
        return unitPriceCurrency;
    }

    public void setUnitPriceCurrency(String unitPriceCurrency) {
        this.unitPriceCurrency = unitPriceCurrency;
    }
}
