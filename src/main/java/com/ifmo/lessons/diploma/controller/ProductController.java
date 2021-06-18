package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by User on 14.06.2021.
 */
@Controller
@RequestMapping("/product")
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

    @GetMapping("/list")
    public String list(Model model) {
        List<Product> products = service.getAll();
        model.addAttribute("title", "Products");
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/add")
    public String add(Product product) {
        return "product-add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        Product product = service.getById(id);
        model.addAttribute("product", product);
        return "product-add";
    }

    @PostMapping("/save/{id}")
    public String save(@PathVariable("id") int id, Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "product-add";
        }
        service.save(product);
        return "redirect:/product/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, Model model) {
        Product product = service.getById(id);
        service.delete(product);
        return "redirect:/product/list";
    }
}
