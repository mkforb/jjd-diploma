package com.ifmo.lessons.diploma.repository;

import com.ifmo.lessons.diploma.entity.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by User on 15.06.2021.
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
}
