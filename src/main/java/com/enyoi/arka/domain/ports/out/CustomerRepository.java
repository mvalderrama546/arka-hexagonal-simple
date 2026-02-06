package com.enyoi.arka.domain.ports.out;

import com.enyoi.arka.domain.entities.Customer;
import com.enyoi.arka.domain.valueobjects.CustomerId;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(CustomerId id);
    List<Customer> findAll();
    void delete(CustomerId id);
    boolean existsById(CustomerId id);
}
