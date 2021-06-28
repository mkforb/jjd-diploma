package com.ifmo.lessons.diploma.form;

import com.ifmo.lessons.diploma.entity.CustomerProduct;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by User on 28.06.2021.
 */
@Getter
@Setter
public class CustomerProductExcel {
    private int customerId;
    private String customerName;
    private int productId;
    private String productName;
    private String number;
    private double price;

    public CustomerProductExcel() {
    }

    public CustomerProductExcel(CustomerProduct cp) {
        this.customerId = cp.getCustomer().getId();
        this.customerName = cp.getCustomer().getName();
        this.productId = cp.getProduct().getId();
        this.productName = cp.getProduct().getName();
        this.number = cp.getNumber();
        this.price = cp.getPrice();
    }
}
