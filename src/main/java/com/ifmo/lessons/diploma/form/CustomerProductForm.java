package com.ifmo.lessons.diploma.form;

import com.ifmo.lessons.diploma.entity.Product;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 29.06.2021.
 */
@Getter
@Setter
public class CustomerProductForm extends AbstractForm {
    private int cId;
    private String cName;
    private int pId;
    @NotEmpty
    private String number;
    private double price;
    private List<Product> products = new ArrayList<>();
}
