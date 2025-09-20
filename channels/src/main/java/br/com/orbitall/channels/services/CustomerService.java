package br.com.orbitall.channels.services;

import br.com.orbitall.channels.canonicals.CustomerInput;
import br.com.orbitall.channels.canonicals.CustomerOutput;
import br.com.orbitall.channels.exceptions.ResourceNotFoundException;
import br.com.orbitall.channels.models.Customer;
import br.com.orbitall.channels.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public CustomerOutput create(CustomerInput input) {
        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFullName(input.fullName());
        customer.setEmail(input.email());
        customer.setPhone(input.phone());
        customer.setCreatedAt(now);
       customer.setUpdatedAt(now);
        customer.setActive(true);

        repository.save(customer);

        return toOutput(customer);
    }

    public CustomerOutput retrieve(UUID id) {
        Customer customer = repository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found (id: " + id + ")"));

        return toOutput(customer);
    }

    public CustomerOutput update(UUID id, CustomerInput input) {
        Customer fetched = repository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found (id: " + id + ")"));

        fetched.setFullName(input.fullName());
        fetched.setEmail(input.email());
        fetched.setPhone(input.phone());
        fetched.setUpdatedAt(LocalDateTime.now());

        repository.save(fetched);

        return toOutput(fetched);
    }

    public CustomerOutput delete(UUID id) {
        Customer fetched = repository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found (id: " + id + ")"));

        fetched.setUpdatedAt(LocalDateTime.now());
        fetched.setActive(false);

        repository.save(fetched);

        return toOutput(fetched);
    }

    public List<CustomerOutput> findAll() {
        List<CustomerOutput> list = new ArrayList<>();

        repository.findAll().forEach(customer -> {
            if (customer.isActive()) {
                list.add(toOutput(customer));
            }
        });

        return list;
    }

    private CustomerOutput toOutput(Customer customer) {
        return new CustomerOutput(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.isActive()
        );
    }
}
