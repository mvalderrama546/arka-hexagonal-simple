package com.enyoi.arka.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductId - Value Object")
class ProductIdTest {

    @Nested
    @DisplayName("of()")
    class OfTests {

        @Test
        @DisplayName("Debe crear ProductId con valor válido")
        void debeCrearProductIdConValorValido() {
            // When
            ProductId productId = ProductId.of("prod-001");

            // Then
            assertThat(productId).isNotNull();
            assertThat(productId.value()).isEqualTo("prod-001");
        }

        @Test
        @DisplayName("Debe lanzar excepción con valor vacío")
        void debeLanzarExcepcionConValorVacio() {
            // When & Then
            assertThatThrownBy(() -> ProductId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("empty");
        }

        @Test
        @DisplayName("Debe lanzar excepción con solo espacios")
        void debeLanzarExcepcionConSoloEspacios() {
            // When & Then
            assertThatThrownBy(() -> ProductId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("empty");
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class IgualdadTests {

        @Test
        @DisplayName("Dos ProductId con mismo valor deben ser iguales")
        void dosProductIdConMismoValorDebenSerIguales() {
            // Given
            ProductId id1 = ProductId.of("prod-001");
            ProductId id2 = ProductId.of("prod-001");

            // Then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("Dos ProductId con diferente valor no deben ser iguales")
        void dosProductIdConDiferenteValorNoDebenSerIguales() {
            // Given
            ProductId id1 = ProductId.of("prod-001");
            ProductId id2 = ProductId.of("prod-002");

            // Then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
