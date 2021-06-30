package com.ifmo.lessons.diploma.controller;

import com.ifmo.lessons.diploma.entity.Customer;
import com.ifmo.lessons.diploma.entity.CustomerProduct;
import com.ifmo.lessons.diploma.entity.Product;
import com.ifmo.lessons.diploma.service.CustomerProductService;
import com.ifmo.lessons.diploma.service.CustomerService;
import com.ifmo.lessons.diploma.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created by User on 15.06.2021.
 */
@Controller
public class HomeController {
    @Autowired
    public HomeController(ProductService productService, CustomerService customerService, CustomerProductService customerProductService) {
        // Test Data
        Product product1 = new Product();
        product1.setName("Prod 1");
        product1.setSku("PROD-001");
        product1.setPrice(105);
        product1.setStock(8);
        product1.setUom("ШТ");
        productService.save(product1);

        Product product2 = new Product();
        product2.setName("Prod 2");
        product2.setSku("PROD-002");
        product2.setPrice(59);
        product2.setStock(0);
        product2.setUom("ШТ");
        productService.save(product2);

        Customer customer1 = new Customer();
        customer1.setName("ООО Продукт Трейдинг");
        customer1.setAddress("СПб, ул. Тверская, 11");
        customerService.save(customer1);

        CustomerProduct customerProduct1 = new CustomerProduct();
        customerProduct1.getKey().setCustomerId(customer1.getId());
        customerProduct1.getKey().setProductId(product1.getId());
        customerProduct1.setCustomer(customer1);
        customerProduct1.setProduct(product1);
        customerProduct1.setNumber("PROD1-AA");
        customerProduct1.setPrice(140);
        customerProductService.save(customerProduct1);

        customerProduct1 = new CustomerProduct();
        customerProduct1.getKey().setCustomerId(customer1.getId());
        customerProduct1.getKey().setProductId(product2.getId());
        customerProduct1.setCustomer(customer1);
        customerProduct1.setProduct(product2);
        customerProduct1.setNumber("PROD2-AA");
        customerProduct1.setPrice(69);
        customerProductService.save(customerProduct1);
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная");
        return "index";
    }
}
