package com.ifmo.lessons.diploma.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * Created by User on 14.06.2021.
 */
@Getter
@Setter
@Entity
@Table(name = "dp_product")
public class Product extends Parent {
    @Column(nullable = false, length = 100)
    @NotEmpty
    private String name;
    @Column(length = 20)
    @NotEmpty
    private String sku;
    private double price;
    private double stock;
    @Column(length = 5)
    @NotEmpty
    private String uom;
}
