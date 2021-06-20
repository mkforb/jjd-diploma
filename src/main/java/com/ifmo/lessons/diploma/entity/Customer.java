package com.ifmo.lessons.diploma.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * Created by User on 15.06.2021.
 */
@Getter
@Setter
@Entity
@Table(name = "dp_customer")
public class Customer extends Parent {
    @Column(nullable = false)
    @NotEmpty
    private String name;
    private String address;
}
