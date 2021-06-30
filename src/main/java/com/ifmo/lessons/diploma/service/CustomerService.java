package com.ifmo.lessons.diploma.service;

import com.ifmo.lessons.diploma.entity.Customer;
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

    @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
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
        // ToDo: проверить есть у клиента товары
        repository.delete(customer);
    }
}
