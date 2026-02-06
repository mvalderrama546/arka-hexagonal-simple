package com.enyoi.arka.domain.entities;

import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.Money;
import com.enyoi.arka.domain.valueobjects.OrderId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.customerId = Objects.requireNonNull(builder.customerId, "customerId is required");
        this.status = builder.status != null ? builder.status : OrderStatus.PENDIENTE;
        this.items = builder.items != null ? new ArrayList<>(builder.items) : new ArrayList<>();
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : this.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void confirm() {
        if (!status.canBeConfirmed()) {
            throw new IllegalStateException("only pending orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMADO;
        this.updatedAt = LocalDateTime.now();
    }

    public void ship() {
        if (!status.canBeShipped()) {
            throw new IllegalStateException("only confirmed orders can be shipped");
        }
        this.status = OrderStatus.EN_DESPACHO;
        this.updatedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (!status.canBeDelivered()) {
            throw new IllegalStateException("only shipped orders can be delivered");
        }
        this.status = OrderStatus.ENTREGADO;
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        if (!isPending()) {
            throw new IllegalStateException("only pending orders can be modified");
        }
        this.items.add(item);
        this.updatedAt = LocalDateTime.now();
    }

    public void remove(OrderItem item) {
        if (!isPending()) {
            throw new IllegalStateException("only pending orders can be removed");
        }
        this.items.remove(item);
        this.updatedAt = LocalDateTime.now();
    }

    public Money getTotal() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.of(java.math.BigDecimal.ZERO, "COP"), Money::add);
    }

    public boolean isPending() {
        return status.isPending();
    }

    public static class Builder {
        private OrderId id;
        private CustomerId customerId;
        private OrderStatus status;
        private List<OrderItem> items;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(OrderId id) {
            this.id = id;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder addItem(OrderItem item) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", status=" + status +
                ", items=" + items.size() +
                ", total=" + getTotal() +
                '}';
    }
}
