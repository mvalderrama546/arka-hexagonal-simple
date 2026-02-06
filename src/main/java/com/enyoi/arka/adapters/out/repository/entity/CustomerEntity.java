package com.enyoi.arka.adapters.out.repository.entity;

import com.enyoi.arka.domain.entities.Customer;
import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.Email;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "city", length = 100)
    private String city;

    public CustomerEntity() {
    }

    public static CustomerEntity fromDomain(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.id = customer.getId().value();
        entity.name = customer.getName();
        entity.lastName = customer.getLastName();
        entity.email = customer.getEmail().value();
        entity.phone = customer.getPhone();
        entity.city = customer.getCity();
        return entity;
    }

    public Customer toDomain() {
        return Customer.builder()
                .id(CustomerId.of(id))
                .name(name)
                .lastName(lastName)
                .email(Email.of(email))
                .phone(phone)
                .city(city)
                .build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
