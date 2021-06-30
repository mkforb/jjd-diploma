package com.ifmo.lessons.diploma.service;

import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.repository.CustomerProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 20.06.2021.
 */
@Service
public class CustomerProductService {
    private final CustomerProductRepository repository;

    @Autowired
    public CustomerProductService(CustomerProductRepository repository) {
        this.repository = repository;
    }

    public List<CustomerProduct> getAll() {
        List<CustomerProduct> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public List<CustomerProduct> getByCustomer(int customerId) {
        return repository.findAllByCustomer(customerId);
    }

    public CustomerProduct getByPk(int cId, int pId) {
        CustomerProduct.CustomerProductKey key = new CustomerProduct.CustomerProductKey();
        key.setCustomerId(cId);
        key.setProductId(pId);
        return repository.findById(key).orElseThrow(IllegalArgumentException::new);
    }

    public void save(CustomerProduct customerProduct) {
        repository.save(customerProduct);
    }

    public void delete(CustomerProduct customerProduct) {
        repository.delete(customerProduct);
    }
}
