package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created by User on 14.06.2021.
 */
@Controller
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;

        // Test
        Product product1 = new Product();
        product1.setName("Prod 1");
        product1.setPrice(105);
        product1.setStock(8);
        service.save(product1);
    }

    @GetMapping("/product-list")
    public String productList(Model model) {
        List<Product> products = service.getAll();
        model.addAttribute("products", products);
        return "product-list";
    }
}
