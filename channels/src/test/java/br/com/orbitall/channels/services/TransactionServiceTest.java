package br.com.orbitall.channels.services;

import br.com.orbitall.channels.canonicals.TransactionInput;
import br.com.orbitall.channels.canonicals.TransactionOutput;
import br.com.orbitall.channels.exceptions.ResourceNotFoundException;
import br.com.orbitall.channels.models.Customer;
import br.com.orbitall.channels.models.Transaction;
import br.com.orbitall.channels.repositories.CustomerRepository;
import br.com.orbitall.channels.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Customer buildCustomer(UUID id, boolean active) {
        Customer c = new Customer();
        c.setId(id);
        c.setFullName("Cliente Teste");
        c.setEmail("cliente@teste.com");
        c.setPhone("+55 11 90000-0000");
        c.setCreatedAt(LocalDateTime.now().minusDays(1));
        c.setUpdatedAt(LocalDateTime.now().minusHours(1));
        c.setActive(active);
        return c;
    }

    private Transaction buildTransaction(UUID id, UUID customerId, boolean active) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setCustomerId(customerId);
        t.setAmount(new BigDecimal("150.75"));
        t.setCardType("VISA");
        t.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        t.setActive(active);
        return t;
        
    }

    @Test
    @DisplayName("create() deve validar cliente e salvar transação")
    void create_shouldValidateCustomerAndSave() {
        UUID customerId = UUID.randomUUID();
        Customer customer = buildCustomer(customerId, true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionInput input = new TransactionInput(customerId, new BigDecimal("200.00"), "MASTERCARD");
        TransactionOutput out = service.create(input);

        assertThat(out).isNotNull();
        assertThat(out.id()).isNotNull();
        assertThat(out.customerId()).isEqualTo(customerId);
        assertThat(out.amount()).isEqualByComparingTo("200.00");
        assertThat(out.cardType()).isEqualTo("MASTERCARD");
        assertThat(out.active()).isTrue();
    }

    @Test
    @DisplayName("create() deve lançar quando cliente não existir ou estiver inativo")
    void create_shouldThrowWhenCustomerMissingOrInactive() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        TransactionInput input = new TransactionInput(customerId, new BigDecimal("200.00"), "ELO");
        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");

        // cliente inativo
        Customer inactive = buildCustomer(customerId, false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("retrieve() deve retornar transação ativa")
    void retrieve_shouldReturnActiveTransaction() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Transaction t = buildTransaction(id, customerId, true);
        when(transactionRepository.findById(id)).thenReturn(Optional.of(t));

        TransactionOutput out = service.retrieve(id);
        assertThat(out.id()).isEqualTo(id);
        assertThat(out.active()).isTrue();
    }

    @Test
    @DisplayName("retrieve() deve lançar quando não encontrada ou inativa")
    void retrieve_shouldThrowWhenNotFoundOrInactive() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.retrieve(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found");

        Transaction inactive = buildTransaction(id, UUID.randomUUID(), false);
        when(transactionRepository.findById(id)).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.retrieve(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findByCustomer() deve retornar apenas transações ativas")
    void findByCustomer_shouldReturnOnlyActive() {
        UUID customerId = UUID.randomUUID();
        Transaction active = buildTransaction(UUID.randomUUID(), customerId, true);
        Transaction inactive = buildTransaction(UUID.randomUUID(), customerId, false);
        when(transactionRepository.findByCustomerId(customerId)).thenReturn(Arrays.asList(active, inactive));

        var list = service.findByCustomer(customerId);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).id()).isEqualTo(active.getId());
    }

    @Test
    @DisplayName("delete() deve marcar transação como inativa e salvar")
    void delete_shouldSoftDelete() {
        UUID id = UUID.randomUUID();
        Transaction existing = buildTransaction(id, UUID.randomUUID(), true);
        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionOutput out = service.delete(id);
        assertThat(out.active()).isFalse();
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
