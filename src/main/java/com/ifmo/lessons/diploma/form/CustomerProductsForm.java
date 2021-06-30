package com.ifmo.lessons.diploma.form;

import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.CustomerProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerProductsForm extends AbstractForm {
    private Customer customer;
    private List<CustomerProduct> list = new ArrayList<>();
}
