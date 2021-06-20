package com.ifmo.lessons.diploma.service;

import com.ifmo.lessons.diploma.entity.CustomerProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerProductForm {
    private List<CustomerProduct> list = new ArrayList<>();
}
