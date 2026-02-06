package com.enyoi.arka.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderId - Value Object")
class OrderIdTest {

    @Nested
    @DisplayName("of()")
    class OfTests {

        @Test
        @DisplayName("Debe crear OrderId con valor válido")
        void debeCrearOrderIdConValorValido() {
            // When
            OrderId orderId = OrderId.of("order-001");

            // Then
            assertThat(orderId).isNotNull();
            assertThat(orderId.value()).isEqualTo("order-001");
        }

        @Test
        @DisplayName("Debe lanzar excepción con valor vacío")
        void debeLanzarExcepcionConValorVacio() {
            // When & Then
            assertThatThrownBy(() -> OrderId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("empty");
        }

        @Test
        @DisplayName("Debe lanzar excepción con solo espacios")
        void debeLanzarExcepcionConSoloEspacios() {
            // When & Then
            assertThatThrownBy(() -> OrderId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("empty");
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class IgualdadTests {

        @Test
        @DisplayName("Dos OrderId con mismo valor deben ser iguales")
        void dosOrderIdConMismoValorDebenSerIguales() {
            // Given
            OrderId id1 = OrderId.of("order-001");
            OrderId id2 = OrderId.of("order-001");

            // Then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("Dos OrderId con diferente valor no deben ser iguales")
        void dosOrderIdConDiferenteValorNoDebenSerIguales() {
            // Given
            OrderId id1 = OrderId.of("order-001");
            OrderId id2 = OrderId.of("order-002");

            // Then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
