package com.enyoi.arka.adapters.out.repository;

import com.enyoi.arka.adapters.out.repository.entity.ProductEntity;
import com.enyoi.arka.domain.entities.Product;
import com.enyoi.arka.domain.entities.ProductCategory;
import com.enyoi.arka.domain.ports.out.ProductRepository;
import com.enyoi.arka.domain.valueobjects.Money;
import com.enyoi.arka.domain.valueobjects.ProductId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaProductRepository implements ProductRepository {

    private final EntityManager entityManager;

    public JpaProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(ProductId.of(entity.getId()))
                .stock(entity.getStock())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(Money.of(entity.getPriceAmount(), entity.getPriceCurrency()))
                .category(entity.getCategory())
                .build();
    }

    @Override
    public Product save(Product product) {
        EntityTransaction tx = entityManager.getTransaction();
        boolean isNewTransaction = !tx.isActive();
        if (isNewTransaction) {
            tx.begin();
        }
        try {
            ProductEntity entity = ProductEntity.fromDomain(product);
            if (entity.getId() == null) {
                entityManager.persist(entity);
            } else {
                entityManager.merge(entity);
            }
            if (isNewTransaction) {
                tx.commit();
            }
            return product;
        } catch (Exception e) {
            if (isNewTransaction && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        ProductEntity entity = entityManager.find(ProductEntity.class, id.value());
        return entity != null ? Optional.of(entity.toDomain()) : Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM ProductEntity p", ProductEntity.class)
                .getResultList()
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(String categoryName) {
        ProductCategory category = ProductCategory.valueOf(categoryName);
        return entityManager.createQuery("SELECT p FROM ProductEntity p WHERE p.category = :category", ProductEntity.class)
                .setParameter("category", category)
                .getResultList()
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return entityManager.createQuery("SELECT p FROM ProductEntity p WHERE p.stock <= :threshold", ProductEntity.class)
                .setParameter("threshold", threshold)
                .getResultList()
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ProductId id) {
        EntityTransaction tx = entityManager.getTransaction();
        boolean isNewTransaction = !tx.isActive();
        if (isNewTransaction) {
            tx.begin();
        }
        try {
            ProductEntity entity = entityManager.find(ProductEntity.class, id.value());
            if (entity != null) {
                entityManager.remove(entity);
            }
            if (isNewTransaction) {
                tx.commit();
            }
        } catch (Exception e) {
            if (isNewTransaction && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean existsById(ProductId id) {
        ProductEntity entity = entityManager.find(ProductEntity.class, id.value());
        return entity != null;
    }
}
