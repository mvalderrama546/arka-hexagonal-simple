package com.enyoi.arka.adapters.out.repository;

import com.enyoi.arka.domain.entities.Customer;
import com.enyoi.arka.domain.valueobjects.CustomerId;
import com.enyoi.arka.domain.valueobjects.Email;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JpaCustomerRepository - Tests de Integración")
class JpaCustomerRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private JpaCustomerRepository repository;

    @BeforeAll
    static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test-persistence-unit");
    }

    @AfterAll
    static void tearDownClass() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        repository = new JpaCustomerRepository(entityManager);
        limpiarBaseDeDatos();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    private void limpiarBaseDeDatos() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM CustomerEntity").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private Customer crearCliente(String id, String name, String email) {
        return Customer.builder()
                .id(CustomerId.of(id))
                .name(name)
                .lastName("Apellido " + name)
                .email(Email.of(email))
                .phone("+57 300 123 4567")
                .build();
    }

    @Nested
    @DisplayName("save()")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar un cliente nuevo correctamente")
        void debeGuardarClienteNuevo() {
            Customer cliente = crearCliente("cust-001", "Juan", "juan@test.com");

            Customer resultado = repository.save(cliente);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId().value()).isEqualTo("cust-001");
            assertThat(resultado.getName()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("Debe actualizar un cliente existente")
        void debeActualizarClienteExistente() {
            Customer cliente = crearCliente("cust-002", "Pedro", "pedro@test.com");
            repository.save(cliente);

            cliente = Customer.builder()
                    .id(CustomerId.of("cust-002"))
                    .name("Pedro Actualizado")
                    .lastName("Nuevo Apellido")
                    .email(Email.of("pedro.nuevo@test.com"))
                    .phone("+57 300 000 0000")
                    .build();
            Customer resultado = repository.save(cliente);

            assertThat(resultado.getName()).isEqualTo("Pedro Actualizado");
            assertThat(resultado.getEmail().value()).isEqualTo("pedro.nuevo@test.com");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Debe encontrar cliente por ID existente")
        void debeEncontrarClientePorId() {
            Customer cliente = crearCliente("cust-003", "Maria", "maria@test.com");
            repository.save(cliente);

            Optional<Customer> resultado = repository.findById(CustomerId.of("cust-003"));

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getName()).isEqualTo("Maria");
            assertThat(resultado.get().getEmail().value()).isEqualTo("maria@test.com");
        }

        @Test
        @DisplayName("Debe retornar vacío para ID inexistente")
        void debeRetornarVacioParaIdInexistente() {
            Optional<Customer> resultado = repository.findById(CustomerId.of("no-existe"));

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay clientes")
        void debeRetornarListaVacia() {
            List<Customer> resultado = repository.findAll();

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("Debe retornar todos los clientes")
        void debeRetornarTodosLosClientes() {
            repository.save(crearCliente("cust-004", "Cliente1", "c1@test.com"));
            repository.save(crearCliente("cust-005", "Cliente2", "c2@test.com"));

            List<Customer> resultado = repository.findAll();

            assertThat(resultado).hasSize(2);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Debe eliminar cliente existente")
        void debeEliminarClienteExistente() {
            Customer cliente = crearCliente("cust-006", "Eliminar", "eliminar@test.com");
            repository.save(cliente);

            repository.delete(CustomerId.of("cust-006"));

            Optional<Customer> resultado = repository.findById(CustomerId.of("cust-006"));
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("No debe lanzar excepción al eliminar cliente inexistente")
        void noDebeLanzarExcepcionAlEliminarInexistente() {
            repository.delete(CustomerId.of("no-existe"));
        }
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsByIdTests {

        @Test
        @DisplayName("Debe retornar true si cliente existe")
        void debeRetornarTrueSiExiste() {
            repository.save(crearCliente("cust-007", "Existe", "existe@test.com"));

            boolean resultado = repository.existsById(CustomerId.of("cust-007"));

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false si cliente no existe")
        void debeRetornarFalseSiNoExiste() {
            boolean resultado = repository.existsById(CustomerId.of("no-existe"));

            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("Persistencia")
    class PersistenciaTests {

        @Test
        @DisplayName("Los datos del cliente deben persistir correctamente")
        void datosDebenPersistir() {
            Customer cliente = Customer.builder()
                    .id(CustomerId.of("cust-008"))
                    .name("Carlos")
                    .lastName("Gomez")
                    .email(Email.of("carlos@test.com"))
                    .phone("+57 310 555 1234")
                    .build();
            repository.save(cliente);

            Optional<Customer> recuperado = repository.findById(CustomerId.of("cust-008"));

            assertThat(recuperado).isPresent();
            assertThat(recuperado.get().getName()).isEqualTo("Carlos");
            assertThat(recuperado.get().getLastName()).isEqualTo("Gomez");
            assertThat(recuperado.get().getEmail().value()).isEqualTo("carlos@test.com");
            assertThat(recuperado.get().getPhone()).isEqualTo("+57 310 555 1234");
        }

        @Test
        @DisplayName("Múltiples clientes deben persistir correctamente")
        void multiplesClientes() {
            for (int i = 1; i <= 5; i++) {
                repository.save(crearCliente("cust-mult-" + i, "Cliente" + i, "cliente" + i + "@test.com"));
            }

            List<Customer> todos = repository.findAll();

            assertThat(todos).hasSize(5);
        }
    }
}
