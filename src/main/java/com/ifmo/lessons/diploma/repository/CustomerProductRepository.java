package com.ifmo.lessons.diploma.repository;

import com.ifmo.lessons.diploma.entity.CustomerProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by User on 20.06.2021.
 */
public interface CustomerProductRepository extends PagingAndSortingRepository<CustomerProduct, CustomerProduct.CustomerProductKey> {
    @Query("SELECT cp FROM CustomerProduct cp WHERE cp.key.customerId = :customerId")
    List<CustomerProduct> findAllByCustomer(int customerId);
}
