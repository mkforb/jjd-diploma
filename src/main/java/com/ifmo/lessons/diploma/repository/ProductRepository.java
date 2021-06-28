package com.ifmo.lessons.diploma.repository;

import com.ifmo.lessons.diploma.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by User on 14.06.2021.
 */
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.sku = :sku")
    Product findBySku(String sku);
}
