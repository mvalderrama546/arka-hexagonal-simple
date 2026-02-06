package com.enyoi.arka.adapters.in;

import com.enyoi.arka.domain.entities.Customer;
import com.enyoi.arka.domain.entities.Order;
import com.enyoi.arka.domain.entities.OrderItem;
import com.enyoi.arka.domain.entities.OrderStatus;
import com.enyoi.arka.domain.entities.Product;
import com.enyoi.arka.domain.entities.ProductCategory;
import com.enyoi.arka.domain.exception.InsufficientStockException;
import com.enyoi.arka.domain.exception.ProductNotFoundException;
import com.enyoi.arka.domain.ports.out.CustomerRepository;
import com.enyoi.arka.domain.ports.out.NotificationService;
import com.enyoi.arka.domain.ports.out.OrderRepository;
import com.enyoi.arka.domain.ports.out.ProductRepository;
import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.Email;
import com.enyoi.arka.domain.valueobjects.Money;
import com.enyoi.arka.domain.valueobjects.OrderId;
import com.enyoi.arka.domain.valueobjects.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl - Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private NotificationService notificationService;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, productRepository, customerRepository, notificationService);
    }

    private Customer crearCliente(String id) {
        return Customer.builder()
                .id(CustomerId.of(id))
                .name("Cliente Test")
                .lastName("Apellido")
                .email(Email.of("cliente@test.com"))
                .phone("123456789")
                .city("Bogotá")
                .build();
    }

    private Product crearProducto(String id, int stock) {
        return Product.builder()
                .id(ProductId.of(id))
                .name("Producto Test")
                .description("Descripción")
                .price(Money.of(new BigDecimal("100000.00"), "COP"))
                .stock(stock)
                .category(ProductCategory.PERIFERICOS)
                .build();
    }

    private OrderItem crearItem(String productoId, int cantidad, int precioUnitario) {
        return OrderItem.builder()
                .productId(ProductId.of(productoId))
                .quantity(cantidad)
                .unitPrice(Money.of(new BigDecimal(precioUnitario), "COP"))
                .build();
    }

    private Order crearOrden(String orderId, String customerId, OrderStatus status, List<OrderItem> items) {
        return Order.builder()
                .id(OrderId.of(orderId))
                .customerId(CustomerId.of(customerId))
                .status(status)
                .items(items)
                .build();
    }

    @Nested
    @DisplayName("createOrder()")
    class CreateOrderTests {

        @Test
        @DisplayName("Debe crear orden exitosamente")
        void debeCrearOrdenExitosamente() {
            // Given
            Customer cliente = crearCliente("cust-001");
            Product producto = crearProducto("prod-001", 100);
            OrderItem item = crearItem("prod-001", 2, 100000);

            when(customerRepository.findById(CustomerId.of("cust-001"))).thenReturn(Optional.of(cliente));
            when(productRepository.findById(ProductId.of("prod-001"))).thenReturn(Optional.of(producto));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Order resultado = orderService.createOrder(CustomerId.of("cust-001"), List.of(item));

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getItems()).hasSize(1);
            verify(orderRepository).save(any(Order.class));
            verify(productRepository).save(any(Product.class));
            verify(notificationService).notifyOrderStatusChange(anyString(), anyString(), eq("PENDIENTE"));
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            // Given
            when(customerRepository.findById(any())).thenReturn(Optional.empty());
            OrderItem item = crearItem("prod-001", 2, 100000);

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(CustomerId.of("no-existe"), List.of(item)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Customer not found");
        }

        @Test
        @DisplayName("Debe lanzar excepción si producto no existe")
        void debeLanzarExcepcionSiProductoNoExiste() {
            // Given
            Customer cliente = crearCliente("cust-001");
            when(customerRepository.findById(CustomerId.of("cust-001"))).thenReturn(Optional.of(cliente));
            when(productRepository.findById(any())).thenReturn(Optional.empty());
            OrderItem item = crearItem("no-existe", 2, 100000);

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(CustomerId.of("cust-001"), List.of(item)))
                    .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción si stock insuficiente")
        void debeLanzarExcepcionSiStockInsuficiente() {
            // Given
            Customer cliente = crearCliente("cust-001");
            Product producto = crearProducto("prod-001", 5);
            OrderItem item = crearItem("prod-001", 10, 100000);

            when(customerRepository.findById(CustomerId.of("cust-001"))).thenReturn(Optional.of(cliente));
            when(productRepository.findById(ProductId.of("prod-001"))).thenReturn(Optional.of(producto));

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(CustomerId.of("cust-001"), List.of(item)))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessageContaining("Insufficient stock");
        }
    }

    @Nested
    @DisplayName("getOrderById()")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Debe retornar orden existente")
        void debeRetornarOrdenExistente() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.PENDIENTE, List.of());
            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));

            // When
            Order resultado = orderService.getOrderById(OrderId.of("order-001"));

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId().value()).isEqualTo("order-001");
        }

        @Test
        @DisplayName("Debe lanzar excepción si orden no existe")
        void debeLanzarExcepcionSiOrdenNoExiste() {
            // Given
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.getOrderById(OrderId.of("no-existe")))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Order not found");
        }
    }

    @Nested
    @DisplayName("getOrdersByCustomerId()")
    class GetOrdersByCustomerIdTests {

        @Test
        @DisplayName("Debe retornar órdenes del cliente")
        void debeRetornarOrdenesDelCliente() {
            // Given
            List<Order> ordenes = List.of(
                    crearOrden("order-001", "cust-001", OrderStatus.PENDIENTE, List.of()),
                    crearOrden("order-002", "cust-001", OrderStatus.CONFIRMADO, List.of())
            );
            when(orderRepository.findByCustomerId(CustomerId.of("cust-001"))).thenReturn(ordenes);

            // When
            List<Order> resultado = orderService.getOrdersByCustomerId(CustomerId.of("cust-001"));

            // Then
            assertThat(resultado).hasSize(2);
            verify(orderRepository).findByCustomerId(CustomerId.of("cust-001"));
        }
    }

    @Nested
    @DisplayName("confirmOrder()")
    class ConfirmOrderTests {

        @Test
        @DisplayName("Debe confirmar orden pendiente")
        void debeConfirmarOrdenPendiente() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.PENDIENTE, List.of());
            Customer cliente = crearCliente("cust-001");

            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(customerRepository.findById(CustomerId.of("cust-001"))).thenReturn(Optional.of(cliente));

            // When
            Order resultado = orderService.confirmOrder(OrderId.of("order-001"));

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getStatus()).isEqualTo(OrderStatus.CONFIRMADO);
            verify(orderRepository).save(any(Order.class));
            verify(notificationService).notifyOrderStatusChange(anyString(), anyString(), eq("CONFIRMADO"));
        }

        @Test
        @DisplayName("Debe fallar al confirmar orden no pendiente")
        void debeFallarAlConfirmarOrdenNoPendiente() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.ENTREGADO, List.of());
            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));

            // When & Then
            assertThatThrownBy(() -> orderService.confirmOrder(OrderId.of("order-001")))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("addItemToOrder()")
    class AddItemToOrderTests {

        @Test
        @DisplayName("Debe agregar item a orden pendiente")
        void debeAgregarItemAOrdenPendiente() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.PENDIENTE, List.of());
            Product producto = crearProducto("prod-001", 100);
            OrderItem item = crearItem("prod-001", 2, 100000);

            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));
            when(productRepository.findById(ProductId.of("prod-001"))).thenReturn(Optional.of(producto));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Order resultado = orderService.addItemToOrder(OrderId.of("order-001"), item);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getItems()).hasSize(1);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Debe fallar al agregar item a orden no pendiente")
        void debeFallarAlAgregarItemAOrdenNoPendiente() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.CONFIRMADO, List.of());
            OrderItem item = crearItem("prod-001", 2, 100000);

            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));

            // When & Then
            assertThatThrownBy(() -> orderService.addItemToOrder(OrderId.of("order-001"), item))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Can only modify pending orders");
        }
    }

    @Nested
    @DisplayName("removeItemFromOrder()")
    class RemoveItemFromOrderTests {

        @Test
        @DisplayName("Debe remover item de orden pendiente")
        void debeRemoverItemDeOrdenPendiente() {
            // Given
            OrderItem item = crearItem("prod-001", 2, 100000);
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.PENDIENTE, List.of(item));
            Product producto = crearProducto("prod-001", 100);

            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));
            when(productRepository.findById(ProductId.of("prod-001"))).thenReturn(Optional.of(producto));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Order resultado = orderService.removeItemFromOrder(OrderId.of("order-001"), item);

            // Then
            assertThat(resultado).isNotNull();
            verify(productRepository).save(any(Product.class));
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("shipOrder()")
    class ShipOrderTests {

        @Test
        @DisplayName("Debe enviar orden confirmada")
        void debeEnviarOrdenConfirmada() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.CONFIRMADO, List.of());
            Customer cliente = crearCliente("cust-001");

            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(customerRepository.findById(CustomerId.of("cust-001"))).thenReturn(Optional.of(cliente));

            // When
            Order resultado = orderService.shipOrder(OrderId.of("order-001"));

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getStatus()).isEqualTo(OrderStatus.EN_DESPACHO);
            verify(notificationService).notifyOrderStatusChange(anyString(), anyString(), eq("EN_DESPACHO"));
        }
    }

    @Nested
    @DisplayName("deliverOrder()")
    class DeliverOrderTests {

        @Test
        @DisplayName("Debe entregar orden en despacho")
        void debeEntregarOrdenEnDespacho() {
            // Given
            Order orden = crearOrden("order-001", "cust-001", OrderStatus.EN_DESPACHO, List.of());
            Customer cliente = crearCliente("cust-001");

            when(orderRepository.findById(OrderId.of("order-001"))).thenReturn(Optional.of(orden));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(customerRepository.findById(CustomerId.of("cust-001"))).thenReturn(Optional.of(cliente));

            // When
            Order resultado = orderService.deliverOrder(OrderId.of("order-001"));

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getStatus()).isEqualTo(OrderStatus.ENTREGADO);
            verify(notificationService).notifyOrderStatusChange(anyString(), anyString(), eq("ENTREGADO"));
        }
    }

    @Nested
    @DisplayName("getAllOrders()")
    class GetAllOrdersTests {

        @Test
        @DisplayName("Debe retornar todas las órdenes")
        void debeRetornarTodasLasOrdenes() {
            // Given
            List<Order> ordenes = List.of(
                    crearOrden("order-001", "cust-001", OrderStatus.PENDIENTE, List.of()),
                    crearOrden("order-002", "cust-002", OrderStatus.CONFIRMADO, List.of())
            );
            when(orderRepository.findAll()).thenReturn(ordenes);

            // When
            List<Order> resultado = orderService.getAllOrders();

            // Then
            assertThat(resultado).hasSize(2);
            verify(orderRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Debe fallar con OrderRepository null")
        void debeFallarConOrderRepositoryNull() {
            assertThatThrownBy(() -> new OrderServiceImpl(null, productRepository, customerRepository, notificationService))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Debe fallar con ProductRepository null")
        void debeFallarConProductRepositoryNull() {
            assertThatThrownBy(() -> new OrderServiceImpl(orderRepository, null, customerRepository, notificationService))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Debe fallar con CustomerRepository null")
        void debeFallarConCustomerRepositoryNull() {
            assertThatThrownBy(() -> new OrderServiceImpl(orderRepository, productRepository, null, notificationService))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Debe fallar con NotificationService null")
        void debeFallarConNotificationServiceNull() {
            assertThatThrownBy(() -> new OrderServiceImpl(orderRepository, productRepository, customerRepository, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
