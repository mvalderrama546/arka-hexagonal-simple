package com.enyoi.arka.adapters.out.repository;

import com.enyoi.arka.adapters.out.repository.entity.OrderEntity;
import com.enyoi.arka.domain.entities.Order;
import com.enyoi.arka.domain.entities.OrderStatus;
import com.enyoi.arka.domain.ports.out.OrderRepository;
import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.OrderId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaOrderRepository implements OrderRepository {
    private final EntityManager entityManager;

    public JpaOrderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Order save(Order order) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrderEntity entity = OrderEntity.fromDomain(order);
            if (entity.getId() == null) {
                entityManager.persist(entity);
            } else {
                entityManager.merge(entity);
            }
            tx.commit();
            return order;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        OrderEntity entity = entityManager.find(OrderEntity.class, id.value());
        return entity != null ? Optional.of(entity.toDomain()) : Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return entityManager.createQuery("SELECT o FROM OrderEntity o", OrderEntity.class)
                .getResultList()
                .stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return entityManager.createQuery("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId", OrderEntity.class)
                .setParameter("customerId", customerId.value())
                .getResultList()
                .stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return entityManager.createQuery("SELECT o FROM OrderEntity o WHERE o.status = :status", OrderEntity.class)
                .setParameter("status", status)
                .getResultList()
                .stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findPendingOrders() {
        return entityManager.createQuery("SELECT o FROM OrderEntity o WHERE o.status = :status", OrderEntity.class)
                .setParameter("status", OrderStatus.PENDIENTE)
                .getResultList()
                .stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(OrderId id) {
        deleteById(id);
    }

    @Override
    public void deleteById(OrderId id) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            OrderEntity entity = entityManager.find(OrderEntity.class, id.value());
            if (entity != null) {
                entityManager.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean existsById(OrderId id) {
        OrderEntity entity = entityManager.find(OrderEntity.class, id.value());
        return entity != null;
    }
}
