package com.enyoi.arka.domain.entities;

import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.Email;

import java.util.Objects;

public class Customer {
    private final CustomerId id;
    private final String name;
    private final String lastName;
    private final Email email;
    private final String phone;
    private final String city;

    private Customer(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.name = Objects.requireNonNull(builder.name, "name is required");
        this.lastName = builder.lastName;
        this.email = Objects.requireNonNull(builder.email, "email is required");
        this.phone = builder.phone;
        this.city = builder.city;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public Email getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public String getFullName() {
        return name + (lastName != null ? " " + lastName : "");
    }

    public static class Builder {
        private CustomerId id;
        private String name;
        private String lastName;
        private Email email;
        private String phone;
        private String city;

        public Builder id(CustomerId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(Email email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", city=" + city +
                '}';
    }
}
