package com.enyoi.arka.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CustomerId - Value Object")
class CustomerIdTest {

    @Nested
    @DisplayName("of()")
    class OfTests {

        @Test
        @DisplayName("Debe crear CustomerId con valor válido")
        void debeCrearCustomerIdConValorValido() {
            // When
            CustomerId customerId = CustomerId.of("cust-001");

            // Then
            assertThat(customerId).isNotNull();
            assertThat(customerId.value()).isEqualTo("cust-001");
        }

        @Test
        @DisplayName("Debe lanzar excepción con valor vacío")
        void debeLanzarExcepcionConValorVacio() {
            // When & Then
            assertThatThrownBy(() -> CustomerId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("empty");
        }

        @Test
        @DisplayName("Debe lanzar excepción con solo espacios")
        void debeLanzarExcepcionConSoloEspacios() {
            // When & Then
            assertThatThrownBy(() -> CustomerId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("empty");
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class IgualdadTests {

        @Test
        @DisplayName("Dos CustomerId con mismo valor deben ser iguales")
        void dosCustomerIdConMismoValorDebenSerIguales() {
            // Given
            CustomerId id1 = CustomerId.of("cust-001");
            CustomerId id2 = CustomerId.of("cust-001");

            // Then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("Dos CustomerId con diferente valor no deben ser iguales")
        void dosCustomerIdConDiferenteValorNoDebenSerIguales() {
            // Given
            CustomerId id1 = CustomerId.of("cust-001");
            CustomerId id2 = CustomerId.of("cust-002");

            // Then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
