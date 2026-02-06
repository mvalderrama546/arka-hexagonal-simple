package com.enyoi.arka.adapters.out.repository;

import com.enyoi.arka.adapters.out.repository.entity.CustomerEntity;
import com.enyoi.arka.domain.entities.Customer;
import com.enyoi.arka.domain.ports.out.CustomerRepository;
import com.enyoi.arka.domain.valueobjects.CustomerId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaCustomerRepository implements CustomerRepository {
    private final EntityManager em;

    public JpaCustomerRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Customer save(Customer customer) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CustomerEntity entity = CustomerEntity.fromDomain(customer);
            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            tx.commit();
            return customer;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        CustomerEntity entity = em.find(CustomerEntity.class, id.value());
        return entity != null ? Optional.of(entity.toDomain()) : Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM CustomerEntity c", CustomerEntity.class)
                .getResultList()
                .stream()
                .map(CustomerEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CustomerId id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CustomerEntity entity = em.find(CustomerEntity.class, id.value());
            if (entity != null) {
                em.remove(entity);
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
    public boolean existsById(CustomerId id) {
        CustomerEntity entity = em.find(CustomerEntity.class, id.value());
        return entity != null;
    }
}
