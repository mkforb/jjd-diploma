package com.ifmo.lessons.diploma.service;

import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.repository.CustomerProductRepository;
import com.ifmo.lessons.diploma.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 15.06.2021.
 */
@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerProductRepository customerProductRepository;

    @Autowired
    public CustomerService(CustomerRepository repository, CustomerProductRepository customerProductRepository) {
        this.repository = repository;
        this.customerProductRepository = customerProductRepository;
    }

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public Customer getById(int id) {
        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public Customer getByName(String name) {
        return repository.findByName(name);
    }

    public void save(Customer customer) {
        repository.save(customer);
    }

    public void delete(Customer customer) {
        // Проверить есть ли у клиента товары
        List<CustomerProduct> list = customerProductRepository.findAllByCustomer(customer.getId());
        if (!list.isEmpty()) {
            throw new IllegalArgumentException("У клиента есть товары. Удаление невозможно.");
        }
        repository.delete(customer);
    }
}
