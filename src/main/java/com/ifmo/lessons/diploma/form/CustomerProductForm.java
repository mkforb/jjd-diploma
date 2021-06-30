package com.ifmo.lessons.diploma.form;

import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.entity.Product;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 29.06.2021.
 */
@Getter
@Setter
public class CustomerProductForm {
    @Valid
    private CustomerProduct customerProduct = new CustomerProduct();
    private boolean productEdit;
    private List<Product> products = new ArrayList<>();
}
