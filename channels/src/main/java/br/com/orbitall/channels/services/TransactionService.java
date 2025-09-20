package br.com.orbitall.channels.services;

import br.com.orbitall.channels.canonicals.TransactionInput;
import br.com.orbitall.channels.canonicals.TransactionOutput;
import br.com.orbitall.channels.exceptions.ResourceNotFoundException;
import br.com.orbitall.channels.models.Customer;
import br.com.orbitall.channels.models.Transaction;
import br.com.orbitall.channels.repositories.CustomerRepository;
import br.com.orbitall.channels.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public TransactionOutput create(TransactionInput input) {
        LocalDateTime now = LocalDateTime.now();

        // Verifica se o cliente existe e está ativo
        Customer customer = customerRepository.findById(input.customerId())
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found (id: " + input.customerId() + ")"));

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setCustomerId(customer.getId());
        transaction.setAmount(input.amount());
        transaction.setCardType(input.cardType());
        transaction.setCreatedAt(now);
        transaction.setActive(true);

        transactionRepository.save(transaction);

        return toOutput(transaction);
    }

    public TransactionOutput retrieve(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(Transaction::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found (id: " + id + ")"));

        return toOutput(transaction);
    }

    public List<TransactionOutput> findByCustomer(UUID customerId) {
        List<TransactionOutput> list = new ArrayList<>();
        transactionRepository.findByCustomerId(customerId)
                .forEach(transaction -> {
                    if (transaction.isActive()) {
                        list.add(toOutput(transaction));
                    }
                });
        return list;
    }

    public TransactionOutput delete(UUID id) {
        Transaction fetched = transactionRepository.findById(id)
                .filter(Transaction::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found (id: " + id + ")"));

        fetched.setActive(false);
        transactionRepository.save(fetched);

        return toOutput(fetched);
    }

    private TransactionOutput toOutput(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getCardType(),
                transaction.getCreatedAt(),
                transaction.isActive()
        );
    }
}
