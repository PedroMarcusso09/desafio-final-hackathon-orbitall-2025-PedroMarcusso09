package br.com.orbitall.channels.controllers;

import br.com.orbitall.channels.canonicals.CustomerInput;
import br.com.orbitall.channels.canonicals.CustomerOutput;
import br.com.orbitall.channels.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping
    public CustomerOutput create(@Valid @RequestBody CustomerInput input) {
        return service.create(input);
    }

    @GetMapping("/{id}")
    public CustomerOutput retrieve(@PathVariable UUID id) {
        return service.retrieve(id);
    }

    @PutMapping("/{id}")
    public CustomerOutput update(@PathVariable UUID id, @RequestBody CustomerInput input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    public CustomerOutput delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @GetMapping
    public List<CustomerOutput> findAll() {
        return service.findAll();
    }
}


