package br.com.orbitall.channels.repositories;

import br.com.orbitall.channels.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    // Buscar todas as transações de um cliente
    List<Transaction> findByCustomerId(UUID customerId);
}
