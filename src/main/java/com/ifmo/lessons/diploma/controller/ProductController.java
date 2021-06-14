package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/product/list")
    public String productList(Model model) {
        List<Product> products = service.getAll();
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/product/add")
    public String productAdd(Product product) {
        return "product-add";
    }

    @PostMapping("/product/save")
    public String productSave(Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "product-add";
        }
        service.save(product);
        return "redirect:/product/list";
    }
}
