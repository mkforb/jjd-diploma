package com.ifmo.lessons.diploma.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by User on 14.06.2021.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "dp_product")
public class Product extends Parent {
    @Column(nullable = false)
    private String name;
    @Column
    private double price;
    @Column
    private int stock;
}