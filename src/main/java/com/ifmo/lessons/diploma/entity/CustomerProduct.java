package com.ifmo.lessons.diploma.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by User on 20.06.2021.
 */
@Getter
@Setter
@Entity
@Table(name = "dp_product_customer")
public class CustomerProduct {

    @EmbeddedId
    private CustomerProductKey key = new CustomerProductKey();

    @ManyToOne
    @MapsId("customerId")
    @NotNull
    private Customer customer = new Customer();

    @ManyToOne
    @MapsId("productId")
    @NotNull
    private Product product = new Product();

    @Column(length = 20)
    @NotEmpty
    private String number;
    private double price;

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class CustomerProductKey implements Serializable {
        @Column(name = "customer_id")
        private int customerId;

        @Column(name = "product_id")
        private int productId;
    }
}
