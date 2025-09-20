package br.com.orbitall.channels.repositories;

import br.com.orbitall.channels.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    // Aqui você pode adicionar queries customizadas se precisar
}
