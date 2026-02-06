package com.enyoi.arka.adapters.out.repository.entity;

import com.enyoi.arka.domain.entities.Order;
import com.enyoi.arka.domain.entities.OrderStatus;
import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.OrderId;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public OrderEntity() {
    }

    public static OrderEntity fromDomain(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.id = order.getId().value();
        entity.customerId = order.getCustomerId().value();
        entity.status = order.getStatus();
        entity.createdAt = order.getCreatedAt();
        entity.updatedAt = order.getUpdatedAt();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            entity.items = order.getItems().stream()
                    .map(item -> OrderItemEntity.fromDomain(item, entity))
                    .collect(Collectors.toList());
        }
        return entity;
    }

    public Order toDomain() {
        Order.Builder builder = Order.builder()
                .id(OrderId.of(id))
                .customerId(CustomerId.of(customerId))
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt);

        for (OrderItemEntity item : items) {
            builder.addItem(item.toDomain());
        }

        Order order = builder.build();
        return order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
