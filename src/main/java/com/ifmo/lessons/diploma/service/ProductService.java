package com.ifmo.lessons.diploma.service;

import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.repository.CustomerProductRepository;
import com.ifmo.lessons.diploma.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by User on 14.06.2021.
 */
@Service
public class ProductService {
    private final ProductRepository repository;
    private final CustomerProductRepository customerProductRepository;

    @Autowired
    public ProductService(ProductRepository repository, CustomerProductRepository customerProductRepository) {
        this.repository = repository;
        this.customerProductRepository = customerProductRepository;
    }

    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        repository.findAll().forEach(products::add);
        return products;
    }

    public Product getById(int id) throws IllegalArgumentException {
        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public Product getBySku(String sku) {
        return repository.findBySku(sku);
    }

    public void save(Product product) {
        repository.save(product);
    }

    public void delete(Product product) {
        // Проверить используется ли товар у клиента
        List<CustomerProduct> list = customerProductRepository.findAllByProduct(product.getId());
        if (!list.isEmpty()) {
            throw new IllegalArgumentException("Товар используется у клиента " + list.get(0).getCustomer().getName() + ". Удаление невозможно.");
        }
        repository.delete(product);
    }
}
