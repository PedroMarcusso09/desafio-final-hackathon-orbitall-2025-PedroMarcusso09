package br.com.orbitall.channels.services;

import br.com.orbitall.channels.canonicals.CustomerInput;
import br.com.orbitall.channels.canonicals.CustomerOutput;
import br.com.orbitall.channels.exceptions.ResourceNotFoundException;
import br.com.orbitall.channels.models.Customer;
import br.com.orbitall.channels.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Customer buildCustomer(UUID id, boolean active) {
        Customer c = new Customer();
        c.setId(id);
        c.setFullName("Maria Silva");
        c.setEmail("maria@example.com");
        c.setPhone("+55 11 90000-0000");
        c.setCreatedAt(LocalDateTime.now().minusDays(1));
        c.setUpdatedAt(LocalDateTime.now().minusHours(1));
        c.setActive(active);
        return c;
    }

    @Test
    @DisplayName("create() deve salvar e retornar CustomerOutput com dados corretos")
    void create_shouldSaveAndReturnOutput() {
        CustomerInput input = new CustomerInput("João da Silva", "joao@example.com", "+55 11 98888-7777");

        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerOutput out = service.create(input);

        assertThat(out).isNotNull();
        assertThat(out.id()).isNotNull();
        assertThat(out.fullName()).isEqualTo("João da Silva");
        assertThat(out.email()).isEqualTo("joao@example.com");
        assertThat(out.phone()).isEqualTo("+55 11 98888-7777");
        assertThat(out.active()).isTrue();
        assertThat(out.createdAt()).isNotNull();
        assertThat(out.updatedAt()).isNotNull();

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(repository).save(captor.capture());
        Customer saved = captor.getValue();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFullName()).isEqualTo(input.fullName());
        assertThat(saved.getEmail()).isEqualTo(input.email());
        assertThat(saved.getPhone()).isEqualTo(input.phone());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    @DisplayName("retrieve() deve retornar o cliente quando ativo")
    void retrieve_shouldReturnActiveCustomer() {
        UUID id = UUID.randomUUID();
        Customer c = buildCustomer(id, true);
        when(repository.findById(id)).thenReturn(Optional.of(c));

        CustomerOutput out = service.retrieve(id);

        assertThat(out.id()).isEqualTo(id);
        assertThat(out.active()).isTrue();
    }

    @Test
    @DisplayName("retrieve() deve lançar ResourceNotFoundException quando não encontrado ou inativo")
    void retrieve_shouldThrowWhenNotFoundOrInactive() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.retrieve(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");

        // caso inativo
        Customer inactive = buildCustomer(id, false);
        when(repository.findById(id)).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.retrieve(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update() deve atualizar campos e salvar")
    void update_shouldUpdateAndSave() {
        UUID id = UUID.randomUUID();
        Customer existing = buildCustomer(id, true);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerInput input = new CustomerInput("Nome Atualizado", "novo@example.com", "+55 21 97777-6666");
        CustomerOutput out = service.update(id, input);

        assertThat(out.fullName()).isEqualTo("Nome Atualizado");
        assertThat(out.email()).isEqualTo("novo@example.com");
        assertThat(out.phone()).isEqualTo("+55 21 97777-6666");

        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("delete() deve marcar como inativo e salvar")
    void delete_shouldSoftDelete() {
        UUID id = UUID.randomUUID();
        Customer existing = buildCustomer(id, true);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerOutput out = service.delete(id);

        assertThat(out.active()).isFalse();
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("findAll() deve retornar apenas clientes ativos")
    void findAll_shouldReturnOnlyActive() {
        Customer active = buildCustomer(UUID.randomUUID(), true);
        Customer inactive = buildCustomer(UUID.randomUUID(), false);
        when(repository.findAll()).thenReturn(Arrays.asList(active, inactive));

        var list = service.findAll();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).id()).isEqualTo(active.getId());
    }
}
